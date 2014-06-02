package cfg;

import java.util.Set;

public class RandomNameGenerator {
    final static String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890";

    final static java.util.Random rand = new java.util.Random();

    public static String generate(Set<String> identifiers, Set<String> terminals){
        StringBuilder builder = new StringBuilder();
        while(builder.toString().length() == 0) {
            int length = rand.nextInt(5)+2;
            for(int i = 0; i < length; i++)
                builder.append(lexicon.charAt(rand.nextInt(lexicon.length())));
            if(identifiers.contains(builder.toString())){
                builder = new StringBuilder();
            }

            for(String key : identifiers){
                if(builder.toString().contains(key) || key.contains(builder.toString())){
                    builder = new StringBuilder();
                }
            }

            for(String terminal : terminals){
                if(builder.toString().contains(terminal)){
                    builder = new StringBuilder();
                }
            }
        }
        return builder.toString();
    }
}
