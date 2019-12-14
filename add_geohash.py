from __future__ import print_function
import io
import Geohash

index = 0
filepath = 'TABLE_LOCATION.sql'
with open(filepath) as fp:
    line = fp.readline()
    cnt = 1
    while line:
        if cnt >= 10:
            for word in line.split("\'"):
                if word not in ['(',',','),\n','\n',');']:
                    if index == 0:
                        District = word
                        index += 1
                    elif index == 1:
                        Neighborhood = word
                        index += 1
                    elif index == 2:
                        lon = word
                        index += 1
                    elif index == 3:
                        lat = word
                        index = 0
            if lon == '' or lat == '':
                geohash = ''
            else:
                geohash = Geohash.encode(float(lat), float(lon))
            print("('"+ District + "\',\'" + Neighborhood + "\',\'" + str(geohash)+"\'),")
        else:
            print(line, end='')
        line = fp.readline()
        cnt += 1