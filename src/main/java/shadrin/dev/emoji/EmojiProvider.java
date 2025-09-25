package shadrin.dev.emoji;

import shadrin.dev.config.AnimalType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * класс-маппер для преобразования типов животных в эмодзи
 */
public class EmojiProvider {

    private static final Map<AnimalType,String> emojiMap = new ConcurrentHashMap<>();
    private static final String PLANTS_EMOJI = "🌿";
    static {
        emojiMap.put(AnimalType.WOLF, "🐺");
        emojiMap.put(AnimalType.BEAR,"🐻");
        emojiMap.put(AnimalType.BOA,"🐍");
        emojiMap.put(AnimalType.DUCK,"🦆");
        emojiMap.put(AnimalType.HORSE,"🐎");
        emojiMap.put(AnimalType.WILD_BOAR, "🐗");
        emojiMap.put(AnimalType.SHEEP,"🐑");
        emojiMap.put(AnimalType.GOAT,"🐐");
        emojiMap.put(AnimalType.FOX,"🦊");
        emojiMap.put(AnimalType.EAGLE,"🦅");
        emojiMap.put(AnimalType.HARE,"🐇");
        emojiMap.put(AnimalType.HAMSTER,"🐁");
        emojiMap.put(AnimalType.CATERPILLAR,"🐛");
        emojiMap.put(AnimalType.BUFFALO,"🐃");
        emojiMap.put(AnimalType.COW,"🐄");
        emojiMap.put(AnimalType.KANGAROO,"🦘");
        emojiMap.put(AnimalType.DEER,"🦌");

    }



    public static String getEmoji(AnimalType type){
        String symbol = emojiMap.get(type);
        if(symbol == null){
            throw new IllegalArgumentException("Symbol is not on the map!");
        } else {
            return symbol;
        }
    }

    public static String getEmojiPlant(){
        String symbol = PLANTS_EMOJI;
        if(symbol == null) {
            throw new IllegalArgumentException("Symbol is not on the map!");
        } else {
            return symbol;
        }
    }

}
