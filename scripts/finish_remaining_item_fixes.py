from remaining_port_helpers import *
p=main/'util/AltChecker.java';s=p.read_text(encoding='utf-8').replace('      readAltsMap();','      readAltsMap();\n      if(alts.isEmpty())return false;');write(p,s)
p=main/'item/william/DictionaryDownloader.java';s=p.read_text(encoding='utf-8').replace('  future.thenAccept(consumer);if(future.isCompletedExceptionally())downloads.remove(address,future);','  future.whenComplete((value,error)->{if(error!=null)downloads.remove(address,future);else consumer.accept(value);});');write(p,s)
write(client/'render/gui/RenderWilliamStats.java',(orig/'render/gui/RenderWilliamStats.java').read_text(encoding='utf-8'))
p=client/'fabric/SixFamilyClient.java';s=p.read_text(encoding='utf-8').replace(' public static void init(){',' public static void init(){\n  net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback.EVENT.register((stack,context,type,lines)->stepsword.mahoutsukai.render.gui.RenderWilliamStats.fameStyleToolTip(stack,lines));');write(p,s)
