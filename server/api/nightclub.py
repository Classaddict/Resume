from flask_restful import Resource
from psycopg2 import errors
from flask_restful import request
from flask_restful import reqparse
import json
from .swen_344_db_utils import *

class nightclub(Resource):
    """
        API that handles all the data gathering from the clubs table
    """
    def get(self):
        """
            returns all of the clubs in an array to be displayed on the homepage
        """
        try:
            city=request.args.get("city")
            if city:
                sql="""
                SELECT * FROM clubs WHERE city=%s ORDER BY id ASC;
                """
                rows = exec_get_all(sql,(city,))
            else:
                sql="""
                    SELECT * FROM clubs ORDER BY id ASC;
                """
                rows = exec_get_all(sql)
            
            clubs=[]
            for row in rows:
                clubs.append({
                    "id":       row[0],
                    "name":     row[1],
                    "yellow":   row[2],
                    "capacity": row[3],
                    "genre":    row[4],
                    "count":    row[5],
                    "city": row[6]
                })
            return clubs, 200
        except Exception as e:
            print(e)
            return {"error": "Server error"}, 500
        
    def post(self):
        """
            Used for adding a new club 
        """
        try:
            name=request.form.get("name")
            city=request.form.get("city")
            genre = request.form.get("genre")
            capacity=request.form.get("capacity")
            yellow   = request.form.get("yellow")
            if not name or not city  or not genre  or not capacity :
                return {"error":"bad request"},400
            sql="""
                INSERT INTO clubs (name,yellow, capacity, genre,city)
                VALUES (%s,%s,%s,%s,%s)
            """
            capacity=int(capacity)
            yellow = int(yellow) if yellow else int(capacity * 0.8)
            exec_commit(sql,(name,yellow,capacity,genre,city))
            return {"sucess":True},200,
        except Exception as e:
            print(e)
            return {"error": "Server error"}, 500
        
    def delete(self,name):
        try:
            check_sql="""
            SELECT name FROM clubs WHERE name=%s
            """
            result=exec_get_one(check_sql,(name,))
            if result is None:
                return {"error":"Club doesnt exist"},404
            sql="""
            DELETE FROM CLUBS WHERE name=%s
            """
            exec_commit(sql,(name,))
            return {"success":"Club removed"},200
        except Exception as e:
            print(e);
            return {"error":"server error"}, 500
        
    def put(self, id):
        """
            API method for editing a club 
        """
        try:
            name     = request.form.get("name")
            city     = request.form.get("city")
            genre    = request.form.get("genre")
            capacity = request.form.get("capacity")
            yellow   = request.form.get("yellow")

            if not name or not genre or not capacity:
                return {"error": "bad request"}, 400

            sql = """
                UPDATE clubs 
                SET name=%s, genre=%s, capacity=%s, yellow=%s
                WHERE id=%s
            """
            exec_commit(sql, (name, genre, int(capacity), int(yellow), id))
            return {"success": True}, 200

        except Exception as e:
            print(e)
            return {"error": "Server error"}, 500
        
