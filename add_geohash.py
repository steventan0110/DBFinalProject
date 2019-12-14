from __future__ import print_function
import io
import Geohash

first = True
filepath = 'TABLE_CRIME_IN.sql'
with open(filepath) as fp:
    line = fp.readline()
    cnt = 1
    while line:
        if cnt >= 9 and line !="('',''),\n":
            for word in line.split("\'"):
                if word not in ['(',',','),\n','\n',');']:
                    if first:
                        lon = word
                    else:
                        lat = word
                    first = not first
            geohash = Geohash.encode(float(lat), float(lon))
            print("('" + lon + "\',\'" + lat + "\',\'" + str(geohash)+"\'),")
        else:
            print(line, end='')
        line = fp.readline()
        cnt += 1