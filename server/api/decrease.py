from flask_restful import Resource
from psycopg2 import errors
from flask_restful import request
from flask_restful import reqparse
import json
from .swen_344_db_utils import *

class decrease(Resource):
    """
        API that to decrease the current count of a club 
    """
    def put(self):
        """
            a put method to decrease the count of a club 
        """
        try:
            name = request.form.get("name")
            if name is None:
                return{"error":"invaild request"},400
            sql="""
                    UPDATE CLUBS SET count = count-1 WHERE NAME =%s 
                    AND count<capacity
                    RETURNING count;
            """
            result=exec_commit(sql,(name,))
            return result[0],200
        except Exception as e:
            print(e)
            return{"error":"server error"},500

