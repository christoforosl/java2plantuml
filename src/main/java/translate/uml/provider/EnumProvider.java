package translate.uml.provider;

public enum EnumProvider{
    PLANT, MERMAID;

    public static EnumProvider fromString(String val) {
        if (val.equals(MERMAID.name())) return MERMAID;
        if (val.equals(PLANT.name())) return PLANT;
        return null;
    }
}