from __future__ import print_function
import io

hash_seen = set() 

index = 0
filepath = 'TABLE_LOCATION_GEO_NODUP.sql'
with open(filepath) as fp:
    line = fp.readline()
    cnt = 1
    while line:
        if cnt >= 9:
            for word in line.split("\'"):
                if word not in ['(',',','),\n','\n',');']:
                    if index == 0:
                        District = word
                        index += 1
                    elif index == 1:
                        Neighborhood = word
                        index += 1
                    elif index == 2:
                        geohash = word
                        index =0
            if geohash not in hash_seen:
                print(line, end='')
                hash_seen.add(geohash)
        else:
            print(line, end='')
        line = fp.readline()
        cnt += 1

        