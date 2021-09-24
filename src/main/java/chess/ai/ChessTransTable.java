package chess.ai;

import static it.unimi.dsi.fastutil.Hash.DEFAULT_LOAD_FACTOR;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

/**
 *
 * Provides a transposition table to store positions in. Uses a primitive int
 * to object hash map provided by the fastutil library.
 */
public class ChessTransTable {
    
    private final Int2ObjectOpenHashMap<TransTableEntry> hashMap;
    private final int mapSize;
    private int hashCollisions;
    
    /**
     * Class constructor.
     * 
     * @param entryCount    size of the hash map
     */
    public ChessTransTable(int entryCount) {
        this.mapSize = entryCount+1;
        this.hashMap = new Int2ObjectOpenHashMap(mapSize, DEFAULT_LOAD_FACTOR); 
        this.hashCollisions = 0;
    }

    /**
     * Inserts an entry into the table; if a position with the same zobris key
     * is already in table, the entry that was searched deeper is stored.
     * 
     * @param newEntry  entry to be stored.
     */
    public void insertEntry(TransTableEntry newEntry){
        
        int key = zobrisToHashMapKey(newEntry.getZobristKey());
        
        /* position is already in table */
        if(hashMap.containsKey(key)){
            TransTableEntry oldEntry = hashMap.get(key);
            /* replacement strategy: store the entry that was searched deeper */
            if(oldEntry.getZobristKey() == newEntry.getZobristKey()) 
                oldEntry.setOldFlag(false);
            if(oldEntry.getOldFlag() || oldEntry.getDepth() <= newEntry
                                                                    .getDepth())
                hashMap.replace(key, newEntry);    
        }
        else hashMap.put(key, newEntry);
    }

    /**
     * Gets table entry represented by the zobris key. Returns null if there is
     * no entry with given zobris key.
     * 
     * @param zobris    key representing the position
     * @return          entry in the table with this key
     */
    public TransTableEntry getEntryByZobrisKey(long zobris){
        
        int key = zobrisToHashMapKey(zobris);
        if(hashMap.containsKey(key)){
            TransTableEntry auxEntry = hashMap.get(key);
            if(auxEntry.getZobristKey() == zobris) return auxEntry;
        }            
        return null;
    }

    private int zobrisToHashMapKey(long zobris){
        return (int) (zobris %mapSize);
    } 

    public int getHashCollisions() {
        return hashCollisions;
    }

    public int getHashFilled(){
        return hashMap.size();
    }
    
    public void resetCollisionCounter(){
        hashCollisions = 0;
    }
    
    /**
     * Clears the hash map.
     */
    void clear() {
        hashMap.clear();
    }
}
