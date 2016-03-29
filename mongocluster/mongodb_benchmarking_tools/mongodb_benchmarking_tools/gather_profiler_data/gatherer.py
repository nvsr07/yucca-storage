from pymongo.database import Database


def gather_profiler_data(mongod_client, database, start, end):
    query = {'ts': {'$gte': start, '$lte': end}}
    db = Database(mongod_client, database)
    return list(db.system.profile.find(query))