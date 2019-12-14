filepath = 'TABLE_CRIME.sql'
with open(filepath) as fp:
    line = fp.readline()
    cnt = 1
    while line:
        if cnt < 13:
            print("Line {}: {}".format(cnt, line.strip()))
            line = fp.readline()
            cnt += 1