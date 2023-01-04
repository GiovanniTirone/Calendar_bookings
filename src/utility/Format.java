package utility;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class Format {

    public static String formatStringMatrix (String mat [][]){
        AtomicInteger maxNumberOfChars = new AtomicInteger();
        Arrays.stream(mat).forEach(row ->{
            Arrays.stream(row).forEach(str -> {
                if(str.length()>maxNumberOfChars.get())
                    maxNumberOfChars.set(str.length());
            });
        });
        int n = mat[0].length;
        int m = mat.length;
        String matStr = "";
        int cn = maxNumberOfChars.get();

        for(int i=0;i<m;i++){
            for(int j=0;j<n;j++) {
                String formatString = "%" +cn +"s     ";
                String newStr = String.format(formatString,mat[i][j]);
                matStr += newStr;
            }
            matStr += "\n";
        }
        return matStr;
    }
}
