package com.github.sdmimaye.rpio.common.utils.io.systeminfo;

import com.github.sdmimaye.rpio.common.utils.io.binary.HexUtil;
import com.google.common.base.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.OperatingSystem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SystemUniqueIdentifier {
    private static final String NEWLINE = System.getProperty("line.separator");

    private static final long uniqueIdentifier;
    private static final String serialnumber;

    private static final File CONFIG = new File("config");
    private static final File SYSTEM_INFO = new File(CONFIG, "system-info.txt");

    private static void doGenerateSystemInfoFileIfRequired() throws IOException {
        if (SYSTEM_INFO.exists() && SYSTEM_INFO.isFile())
            return;

        if (!CONFIG.exists() && !CONFIG.mkdir())
            throw new RuntimeException("Could not generate config folder for system-info file");

        SystemInfo info = new SystemInfo();
        OperatingSystem system = info.getOperatingSystem();
        HardwareAbstractionLayer hardware = info.getHardware();
        CentralProcessor processor = hardware.getProcessor();
        int processorCount = processor.getLogicalProcessorCount();
        NetworkIF[] networkIFs = hardware.getNetworkIFs();

        String manufacturer = system.getManufacturer();
        String version = system.getVersion().getVersion();
        String systemSerialNumber = processor.getSystemSerialNumber();
        String processorIdentifier = processor.getIdentifier();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SYSTEM_INFO))) {
            write(writer, "System-Manufacturer: ", manufacturer);
            write(writer, "System-Version: ", version);
            write(writer, "Serial-Number: ", systemSerialNumber);
            write(writer, "Processor: ", processorIdentifier);
            write(writer, "Processor-Count: ", String.valueOf(processorCount));
            write(writer, "Network-Interfaces", "");

            for (NetworkIF networkIF : networkIFs) {
                write(writer, networkIF.getDisplayName() + ": ", networkIF.getMacaddr());
            }
            writer.flush();
            writer.close();
        }
    }

    private static void write(BufferedWriter writer, String name, String value) throws IOException {
        writer.write(name);
        writer.write(value);
        writer.write(NEWLINE);
    }

    private static long doGenerateUniqueIdentifier() throws IOException {
        doGenerateSystemInfoFileIfRequired();
        String idData = FileUtils.readFileToString(SYSTEM_INFO, Charsets.UTF_8);

        int idUpper = idData.hashCode();
        int idLower = StringUtils.reverse(idData).hashCode();

        return (Integer.toUnsignedLong(idUpper) << 32 | Integer.toUnsignedLong(idLower));
    }

    private static String doGenerateSerialNumber(long id) {
        return HexUtil.toHex(ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(id).array(), HexUtil.SpaceMode.NO_SPACES);
    }

    static {
        try {
            uniqueIdentifier = doGenerateUniqueIdentifier();
            serialnumber = doGenerateSerialNumber(uniqueIdentifier);
        } catch (Exception ioe) {
            throw new RuntimeException("Could not generate unique identifier for this device. Reason: " + ioe.getMessage());
        }
    }

    public static long getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    public static String getSerialNumber() {
        return serialnumber;
    }
}
