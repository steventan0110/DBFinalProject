from __future__ import print_function
import io
import Geohash

index = 0
filepath = 'TABLE_BUILDING_IN.sql'
with open(filepath) as fp:
    line = fp.readline()
    cnt = 1
    while line:
        if cnt >= 9:
            for word in line.split("\'"):
                if word not in ['(',',','),\n','\n',');']:
                    if index == 0:
                        BID = word
                        index += 1
                    elif index == 1:
                        lat = word
                        index += 1
                    elif index == 2:
                        lon = word
                        index = 0
            if lon == '' or lat == '':
                geohash = ''
            else:
                geohash = Geohash.encode(float(lat), float(lon))
            print("('"+ BID + "\',\'" + lon + "\',\'" + lat + "\',\'" + geohash +"\'),")
        else:
            print(line, end='')
        line = fp.readline()
        cnt += 1

        