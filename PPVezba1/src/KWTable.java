import java.util.Hashtable;

public class KWTable {

    private Hashtable<String, Integer> mTable; // Dobra praksa je koristiti generike

    public KWTable() {
        // Inicijalizacija hash tabele koja pamti kljucne reci
        mTable = new Hashtable<>();
        
        // Kljucne reci iz VASEG zadatka
        mTable.put("program", sym.PROGRAM);
        mTable.put("return", sym.RETURN);
        mTable.put("begin", sym.BEGIN);
        mTable.put("end", sym.END);
        mTable.put("integer", sym.INTEGER);
        mTable.put("char", sym.CHAR);
        mTable.put("string", sym.STRING);
        mTable.put("file", sym.FILE);
        mTable.put("read", sym.READ);
        mTable.put("do", sym.DO);
        mTable.put("in", sym.IN);
    }

    /**
     * Vraca ID kljucne reci 
     */
    public int find(String keyword) {
        Object symbol = mTable.get(keyword);
        if (symbol != null) {
            return (Integer) symbol; // Moderniji Java kod automatski radi unboxing
        }
        
        // Ako rec nije pronadjena u tabeli kljucnih reci radi se o identifikatoru
        return sym.ID;
    }
}