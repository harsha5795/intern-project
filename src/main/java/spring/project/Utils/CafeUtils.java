package spring.project.Utils;

import com.google.common.base.Strings;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CafeUtils {
   private CafeUtils(){
       //private constructor so that we cannot create an object
   }

    public static ResponseEntity<String> getResponeEntity(String responseMessage , HttpStatus httpStatus) {
        return new ResponseEntity<String>("{\"messag\":\"" + responseMessage + "\"}", httpStatus);
    }
//  By declaring it as static, We can directly access it with the class name without creating an object.

    public static String getUUID(){
        Date data = new Date();
        long time =  data.getTime();
        return "BILL" + time;
    }
//  used to generate filename

    public static Map<String , Object> getMapFromJson(String data){
        if(!Strings.isNullOrEmpty(data))
            return new Gson().fromJson(data, new TypeToken<Map<String, Object>>(){}.getType());
        return new HashMap<>();
    }
//  Now each element of the JSON array will be converted into a Map

    public static Boolean isFileExist(String path){
        log.info("Inside isFileExist {}" , path);
        try {
            File file = new File(path);
            return (file != null && file.exists()) ? Boolean.TRUE : Boolean.FALSE;

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
}
