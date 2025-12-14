import java.util.Hashtable;

public class KWTable {

    private Hashtable<String, Integer> mTable; 

    public KWTable() {
        
        mTable = new Hashtable<>();
        
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

    
    public int find(String keyword) {
        Object symbol = mTable.get(keyword);
        if (symbol != null) {
            return (Integer) symbol; 
        }
        
        return sym.ID;
    }
}