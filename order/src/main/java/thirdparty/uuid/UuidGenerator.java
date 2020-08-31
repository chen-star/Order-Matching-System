package thirdparty.uuid;

public class UuidGenerator {


    private static UuidGenerator ourInstance = new UuidGenerator();

    public static UuidGenerator getInstance() {
        return ourInstance;
    }

    private UuidGenerator() {
    }

    public void init(long centerId, long workerId) {
        idWorker = new SnowflakeIdWorker(workerId, centerId);
    }

    private SnowflakeIdWorker idWorker;

    public long getUUID() {
        return idWorker.nextId();
    }


}
