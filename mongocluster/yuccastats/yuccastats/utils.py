from datetime import datetime, timedelta

MINTIME = datetime.min.time()


def first_month_day(today):
    """Given a day it provides the first day of the same month"""
    return begin_of_day(today).replace(day=1)


def begin_of_day(today):
    """First second of day in UTC timezone"""
    return datetime.combine(today.date(), MINTIME)


def thirtydays_ago(today):
    """Returns 30 days before the day"""
    return begin_of_day(today) - timedelta(days=30)


def isodate(strdate):
    """Convert a string date to a datetime object"""
    if strdate == 'yesterday':
        return datetime.today() - timedelta(days=1)
    return datetime.strptime(strdate, '%Y-%m-%d')
