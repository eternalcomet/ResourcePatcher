package io.eternalcomet.patcher;

import com.google.gson.*;
import io.eternalcomet.Util;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class QuestPatcher {
    private static final String EXCEL_FILE_NAME = "ExcelBinOutput/QuestExcelConfigData.json";
    private static final String PATCH_FILE_NAME = "QuestPatchData.json";

    public static void patch(File resourceDir, File patchFileDir) {
        File output = new File(resourceDir, "Generated/Quest");
        if (!(output.exists() && output.isDirectory())) {
            output.mkdirs();
        }
        var gson=new GsonBuilder().setPrettyPrinting().create();
        try {
            //load excel
            String excelStr=Files.readString(new File(resourceDir,EXCEL_FILE_NAME).toPath());
            excelStr=excelStr.replace("\"_","\"");
            excelStr=excelStr.replace("param_str","paramStr");
            JsonArray excelJson= JsonParser.parseString(excelStr).getAsJsonArray();
            Int2ObjectMap<JsonObject> subQuests= new Int2ObjectOpenHashMap<>();
            for(var subQuest : excelJson){
                var obj=subQuest.getAsJsonObject();
                subQuests.put(obj.get("subId").getAsInt(),obj);
            }

            //load patch and apply
            var patchJson=Util.parseArray(new File(patchFileDir,PATCH_FILE_NAME));
            for(var patch : patchJson){
                var obj=patch.getAsJsonObject();
                int subId=obj.get("subId").getAsInt();
                var subQuest=subQuests.get(subId);
                for(var e:obj.entrySet()){
                    if(subQuest.has(e.getKey())){
                        subQuest.remove(e.getKey());
                    }
                    subQuest.add(e.getKey(),e.getValue());
                }
            }

            //load bin and apply
            File questPath =new File(resourceDir,"BinOutput/Quest");
            for(var f :questPath.listFiles()){
                String str= Files.readString(f.toPath());
                str=str.replace("param_str","paramStr");
                var quest=JsonParser.parseString(str.replace("\"_","\"")).getAsJsonObject();
                JsonArray sub=quest.getAsJsonArray("subQuests");
                if(sub==null) continue;
                for(var s:sub){
                    var obj=s.getAsJsonObject();
                    int id=obj.get("subId").getAsInt();
                    var p=subQuests.get(id);
                    //p->obj
                    for(var e:p.entrySet()){
                        String key=e.getKey();
                        if(!obj.has(key)) {
                            //add new field
                            obj.add(e.getKey(), e.getValue());
                        }else if(key.endsWith("Cond")||key.endsWith("Exec")){
                            obj.remove(key);
                            obj.add(key,e.getValue());
                        }

                    }
                }
                Path path=new File(output,f.getName()).toPath();
                Files.writeString(path,gson.toJson(quest));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
