from datetime import datetime


def isodate(strdate):
    """Convert a string date to a datetime object"""
    return datetime.strptime(strdate, '%Y-%m-%d')