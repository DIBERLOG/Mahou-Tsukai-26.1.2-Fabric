def synclang(inf,outf):
    file1 = open(inf, 'r') 
    lines = file1.readlines()

    data = {}
    for line in lines:
        arr=[]
        arr+=[line[:line.find("=")]]
        arr+=[line[line.find("=")+1:]]
        data[arr[0]] = arr[1]

    file1.close()
    file2 = open(outf, 'r')
    lines2 = file2.readlines();

    data2 = {}
    for line2 in lines2:
        arr=[]
        arr+=[line2[:line2.find("=")]]
        arr+=[line2[line2.find("=")+1:]]
        data2[arr[0]] = arr[1]
    file2.close()
    print outf + "================================"

    with open(outf, 'a') as outfile:
        for key in sorted(data.keys()):
            if (not key in data2) :
                print (key + "=" +data[key])
                outfile.write(key+"="+data[key])
                if not (data[key][-1] == '\n'):
                    outfile.write('\n')
    outfile.close()    
synclang("en_us.lang", "zh_cn.lang")
#synclang("en_us.lang", "hu_hu.lang")
#synclang("en_us.lang", "de_de.lang")
#synclang("en_us.lang", "la_va.lang")
#synclang("en_us.lang", "ru_ru.lang")
#synclang("en_us.lang", "uk_ua.lang")
