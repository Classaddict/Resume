from flask import Flask
from flask_restful import Resource, Api
from flask_cors import CORS
from api.swen_344_db_utils import *
from api.nightclub import *
from api.increase import *
from api.decrease import *
app = Flask(__name__) #create Flask instance
CORS(app) #Enable CORS on Flask server to work with Nodejs pages
api = Api(app) #api router

api.add_resource(nightclub, '/clubs', endpoint='clubs')
api.add_resource(nightclub, '/clubs/<int:id>', endpoint='clubs_by_id')
api.add_resource(nightclub, '/clubs/<string:name>', endpoint='clubs_by_name')
api.add_resource(increase, '/increase')
api.add_resource(decrease, '/decrease')
if __name__ == '__main__':
    print("Loading db");
    exec_sql_file('clubs.sql')
    print("Starting flask");
    app.run(debug=True, port=5001), #starts Flask
