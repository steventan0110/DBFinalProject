from __future__ import print_function
import io

lines_seen = set() 

index = 0
filepath = 'TABLE_LOCATION_GEO.sql'
with open(filepath) as fp:
    line = fp.readline()
    cnt = 1
    while line:
        if line not in lines_seen:
            print(line, end='')
            lines_seen.add(line)
        line = fp.readline()
        cnt += 1