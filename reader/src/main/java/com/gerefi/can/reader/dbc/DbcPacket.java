package com.gerefi.can.reader.dbc;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Packet describes all the fields for specific can ID
 */
public class DbcPacket {
    private final int id;
    private final String name;
    private final List<DbcField> fields = new ArrayList<>();

    public DbcPacket(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<DbcField> getFields() {
        return fields;
    }

    @Override
    public String toString() {
        return "DbcPacket{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public void add(DbcField dbcField) {
        fields.add(dbcField);
    }

    public DbcField find(String name) {
        for (DbcField field : fields) {
            if (field.getName().equalsIgnoreCase(name))
                return field;
        }
        return null;
    }

    public void replaceName(String originalName, String niceName) {
        if (niceName.charAt(niceName.length() - 1) == ';')
            niceName = niceName.substring(0, niceName.length() - 1);
        niceName = unquote(niceName);
        DbcField field = find(originalName);
        if (field == null) {
            System.err.println("Field not found by [" + originalName + "]");
            return;
        }
        Objects.requireNonNull(field, "By " + originalName);
        field.rename(niceName);
    }

    private static String unquote(String q) {
        final StringBuilder buf = new StringBuilder();
        final int len = q.length();
        if (len < 2 || q.charAt(0) != '"' || q.charAt(len - 1) != '"')
            return q;
        for (int i = 1; i < len - 1; i++) {
            char c = q.charAt(i);
            if (c == '\\') {
                if (i == len - 2)
                    throw new IllegalArgumentException("Trailing backslash");
                c = q.charAt(++i);
                switch (c) {
                    case 'n':
                        c = '\n';
                        break;
                    case '\\':
                    case '\"':
                    case '*':
                    case '?':
                        break;
                    default:
                        throw new IllegalArgumentException(
                                "Bad character '" + c + "' after backslash");
                }
            } else {
                switch (c) {
                    case '*':
                    case '?':
                    case '\"':
                    case '\n':
                        throw new IllegalArgumentException(
                                "Invalid unescaped character '" + c +
                                        "' in the string to unquote");
                }
            }
            buf.append(c);
        }
        return buf.toString();
    }

}
