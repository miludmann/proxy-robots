package orwell.proxy.config;

/**
 * Created by miludmann on 5/5/15.
 */
public class ConfigCli {
    private final String filePath;
    private final EnumConfigFileType enumConfigFileType;

    public ConfigCli(String filePath, EnumConfigFileType enumConfigFileType) {
        this.filePath = filePath;
        this.enumConfigFileType = enumConfigFileType;
    }

    public String getFilePath() {
        return filePath;
    }

    public EnumConfigFileType getEnumConfigFileType() {
        return enumConfigFileType;
    }
}
