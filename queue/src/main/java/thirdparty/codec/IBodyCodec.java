package thirdparty.codec;

public interface IBodyCodec {

    <T> byte[] serialize(T obj) throws Exception;


    <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception;


}