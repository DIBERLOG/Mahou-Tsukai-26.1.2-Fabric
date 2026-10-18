import json
def langToJson(inf, outf):
    file1 = open(inf, 'r') 
    lines = file1.readlines()

    data = {}
    for line in lines:
        arr=[]
        arr+=[line[:line.find("=")]]
        arr+=[line[line.find("=")+1:]]
        if len(arr) == 2:
            if (arr[0].startswith("item.")):
                arr[0]=arr[0].replace("item.", "item.jousting.", 1)
            if (".book." not in arr[0] and ".config." not in arr[0]):
                arr[0]=arr[0].replace(".name","")
            if (".config" in arr[0]):
                arr[0] = arr[0].replace(".name.tooltip", ".comment");
            arr[0]=arr[0].replace("tile.","block.jousting.")
            if (arr[0].startswith("entity.jousting:")):
                arr[0]=arr[0].replace("entity.jousting:","entity.jousting.")
            arr[0]=arr[0].replace("spatial_disorientation_gauntlet", "spatial_disorientation_staff");            
            arr[0]=arr[0].replace("fluid.murky_water", "item.jousting.murky_bucket")
            arr[0]=arr[0].replace("itemGroup.jousting", "itemGroup.jousting")
            data[arr[0]] = arr[1][:-1]
    #print data

    with open(outf, 'w+') as outfile:
        outfile.write(json.dumps(data, indent=2, sort_keys=True))
        outfile.close()

langToJson("en_us.lang", "en_us.json") 
langToJson("zh_cn.lang", "zh_cn.json")
langToJson("pt_br.lang", "pt_br.json")
#langToJson("hu_hu.lang", "hu_hu.json")
#langToJson("de_de.lang", "de_de.json")
#langToJson("la_va.lang", "la_va.json")
langToJson("ru_ru.lang", "ru_ru.json") 
langToJson("ja_jp.lang", "ja_jp.json") 
#langToJson("uk_ua.lang", "uk_ua.json") 

