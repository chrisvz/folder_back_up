import java.util.ArrayList;
import java.util.Arrays;

public class ExcelRow {


    private String[] names;
    private String namingFormat;

    public ExcelRow(String[] names, String namingFormat) {
        this.names = names;
        this.namingFormat = namingFormat;
    }

    @Override
    public String toString() {
        return "ExcelRow{" +
                "names=" + Arrays.toString(names) +
                ", namingFormat='" + namingFormat + '\'' +
                '}';
    }

    public String[] getNames() {
        return names;
    }

    public String getNamingFormat() {
        return namingFormat;
    }
}
