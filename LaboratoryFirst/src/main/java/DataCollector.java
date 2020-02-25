import java.util.HashMap;
import java.util.Map;

public class DataCollector {

    private long symbolOccurrences = 0;
    private Map<String,Long> neighborsOccurrences = new HashMap<>();

    public DataCollector() {
        this.increaseSymbolOccurrences();
    }

    public void increaseSymbolOccurrences(){
        this.symbolOccurrences++;
    }

    public long getSymbolOccurrences() {
        return symbolOccurrences;
    }

    public void setSymbolOccurrences(long symbolOccurrences) {
        this.symbolOccurrences = symbolOccurrences;
    }

    public Map<String, Long> getNeighborsOccurrences() {
        return neighborsOccurrences;
    }

    public void setNeighborsOccurrences(Map<String, Long> neighborsOccurrences) {
        this.neighborsOccurrences = neighborsOccurrences;
    }

    public void updateNeighborsOccurrences(String key) {
        final Long oldValue = neighborsOccurrences.get(key);
        neighborsOccurrences.replace(key, oldValue + 1L);
    }

    public void addNewNeighbor(String key, Long value){
        this.neighborsOccurrences.put(key,value);
    }
}
