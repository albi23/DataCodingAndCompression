import java.util.HashMap;
import java.util.Map;

public class DataCollector {

    private long symbolOccurrences = 0;
    private Map<Byte, Long> neighborsOccurrences = new HashMap<>();

    public DataCollector() {
        this.increaseSymbolOccurrences();
    }

    public DataCollector(long symbolOccurrences) {
        this.symbolOccurrences = symbolOccurrences;
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

    public Map<Byte, Long> getNeighborsOccurrences() {
        return neighborsOccurrences;
    }

    public void setNeighborsOccurrences(Map<Byte, Long> neighborsOccurrences) {
        this.neighborsOccurrences = neighborsOccurrences;
    }

    public void updateNeighborsOccurrences(Byte key) {
        final Long oldValue = neighborsOccurrences.get(key);
        neighborsOccurrences.replace(key, oldValue + 1L);
    }

    public void addNewNeighbor(Byte key, Long value){
        this.neighborsOccurrences.put(key,value);
    }
}
