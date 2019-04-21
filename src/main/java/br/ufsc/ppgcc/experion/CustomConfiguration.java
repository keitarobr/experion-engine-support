package br.ufsc.ppgcc.experion;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ConfigurationDecoder;
import org.apache.commons.configuration2.EnvironmentConfiguration;
import org.apache.commons.configuration2.ImmutableConfiguration;
import org.apache.commons.configuration2.interpol.ConfigurationInterpolator;
import org.apache.commons.configuration2.interpol.Lookup;
import org.apache.commons.configuration2.sync.LockMode;
import org.apache.commons.configuration2.sync.Synchronizer;
import org.apache.commons.lang3.RegExUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class CustomConfiguration implements Configuration {

    private final EnvironmentConfiguration config;

    public CustomConfiguration(EnvironmentConfiguration config) {
        this.config = config;
    }

    private String fixName(String name) {
        return RegExUtils.replaceAll(name, "\\.", "_");
    }

    @Override
    public Configuration subset(String s) {
        return this.config.subset(fixName(s));
    }

    @Override
    public void addProperty(String s, Object o) {
        throw new RuntimeException("Can't add to environment");
    }

    @Override
    public void setProperty(String s, Object o) {
        throw new RuntimeException("Can't add to environment");
    }

    @Override
    public void clearProperty(String s) {
        throw new RuntimeException("Can't add to environment");
    }

    @Override
    public void clear() {
        throw new RuntimeException("Can't add to environment");
    }

    @Override
    public ConfigurationInterpolator getInterpolator() {
        return this.config.getInterpolator();
    }

    @Override
    public void setInterpolator(ConfigurationInterpolator configurationInterpolator) {
        this.config.setInterpolator(configurationInterpolator);
    }

    @Override
    public void installInterpolator(Map<String, ? extends Lookup> map, Collection<? extends Lookup> collection) {
        this.config.installInterpolator(map, collection);
    }

    @Override
    public boolean isEmpty() {
        return this.config.isEmpty();
    }

    @Override
    public int size() {
        return this.config.size();
    }

    @Override
    public boolean containsKey(String s) {
        return this.config.containsKey(fixName(s));
    }

    @Override
    public Object getProperty(String s) {
        return this.config.getProperty(fixName(s));
    }

    @Override
    public Iterator<String> getKeys(String s) {
        return this.config.getKeys(fixName(s));
    }

    @Override
    public Iterator<String> getKeys() {
        List<String> keys = new LinkedList<>();
        this.config.getKeys().forEachRemaining(key -> keys.add(fixName(key)));
        return keys.iterator();
    }

    @Override
    public Properties getProperties(String s) {
        return this.config.getProperties(fixName(s));
    }

    @Override
    public boolean getBoolean(String s) {
        return this.config.getBoolean(fixName(s));
    }

    @Override
    public boolean getBoolean(String s, boolean b) {
        return this.config.getBoolean(fixName(s), b);
    }

    @Override
    public Boolean getBoolean(String s, Boolean aBoolean) {
        return this.config.getBoolean(fixName(s), aBoolean);
    }

    @Override
    public byte getByte(String s) {
        return this.config.getByte(fixName(s));
    }

    @Override
    public byte getByte(String s, byte b) {
        return this.config.getByte(fixName(s), b);
    }

    @Override
    public Byte getByte(String s, Byte aByte) {
        return this.config.getByte(fixName(s), aByte);
    }

    @Override
    public double getDouble(String s) {
        return this.config.getDouble(fixName(s));
    }

    @Override
    public double getDouble(String s, double v) {
        return this.config.getDouble(fixName(s), v);
    }

    @Override
    public Double getDouble(String s, Double aDouble) {
        return this.config.getDouble(fixName(s), aDouble);
    }

    @Override
    public float getFloat(String s) {
        return this.config.getFloat(fixName(s));
    }

    @Override
    public float getFloat(String s, float v) {
        return this.config.getFloat(fixName(s), v);
    }

    @Override
    public Float getFloat(String s, Float aFloat) {
        return this.config.getFloat(fixName(s), aFloat);
    }

    @Override
    public int getInt(String s) {
        return this.config.getInt(fixName(s));
    }

    @Override
    public int getInt(String s, int i) {
        return this.config.getInt(fixName(s), i);
    }

    @Override
    public Integer getInteger(String s, Integer integer) {
        return this.config.getInteger(fixName(s), integer);
    }

    @Override
    public long getLong(String s) {
        return this.config.getLong(fixName(s));
    }

    @Override
    public long getLong(String s, long l) {
        return this.config.getLong(fixName(s), l);
    }

    @Override
    public Long getLong(String s, Long aLong) {
        return this.config.getLong(fixName(s), aLong);
    }

    @Override
    public short getShort(String s) {
        return this.config.getShort(fixName(s));
    }

    @Override
    public short getShort(String s, short i) {
        return this.config.getShort(fixName(s), i);
    }

    @Override
    public Short getShort(String s, Short aShort) {
        return this.config.getShort(fixName(s), aShort);
    }

    @Override
    public BigDecimal getBigDecimal(String s) {
        return this.config.getBigDecimal(fixName(s));
    }

    @Override
    public BigDecimal getBigDecimal(String s, BigDecimal bigDecimal) {
        return this.config.getBigDecimal(fixName(s), bigDecimal);
    }

    @Override
    public BigInteger getBigInteger(String s) {
        return this.config.getBigInteger(fixName(s));
    }

    @Override
    public BigInteger getBigInteger(String s, BigInteger bigInteger) {
        return this.config.getBigInteger(fixName(s), bigInteger);
    }

    @Override
    public String getString(String s) {
        return this.config.getString(fixName(s));
    }

    @Override
    public String getString(String s, String s1) {
        return this.config.getString(fixName(s), s1);
    }

    @Override
    public String getEncodedString(String s, ConfigurationDecoder configurationDecoder) {
        return this.config.getEncodedString(fixName(s), configurationDecoder);
    }

    @Override
    public String getEncodedString(String s) {
        return this.config.getEncodedString(fixName(s));
    }

    @Override
    public String[] getStringArray(String s) {
        return this.config.getStringArray(fixName(s));
    }

    @Override
    public List<Object> getList(String s) {
        return this.config.getList(fixName(s));
    }

    @Override
    public List<Object> getList(String s, List<?> list) {
        return this.config.getList(fixName(s), list);
    }

    @Override
    public <T> T get(Class<T> aClass, String s) {
        return this.config.get(aClass, fixName(s));
    }

    @Override
    public <T> T get(Class<T> aClass, String s, T t) {
        return this.config.get(aClass, fixName(s), t);
    }

    @Override
    public Object getArray(Class<?> aClass, String s) {
        return this.config.getArray(aClass, fixName(s));
    }

    @Override
    public Object getArray(Class<?> aClass, String s, Object o) {
        return this.config.getArray(aClass, fixName(s), o);
    }

    @Override
    public <T> List<T> getList(Class<T> aClass, String s) {
        return this.config.getList(aClass, fixName(s));
    }

    @Override
    public <T> List<T> getList(Class<T> aClass, String s, List<T> list) {
        return this.config.getList(aClass, fixName(s), list);
    }

    @Override
    public <T> Collection<T> getCollection(Class<T> aClass, String s, Collection<T> collection) {
        return this.config.getCollection(aClass, fixName(s), collection);
    }

    @Override
    public <T> Collection<T> getCollection(Class<T> aClass, String s, Collection<T> collection, Collection<T> collection1) {
        return this.config.getCollection(aClass, fixName(s), collection, collection1);
    }

    @Override
    public ImmutableConfiguration immutableSubset(String s) {
        return this.config.immutableSubset(fixName(s));
    }

    @Override
    public Synchronizer getSynchronizer() {
        return this.config.getSynchronizer();
    }

    @Override
    public void setSynchronizer(Synchronizer synchronizer) {
        this.config.setSynchronizer(synchronizer);
    }

    @Override
    public void lock(LockMode lockMode) {
        this.config.lock(lockMode);
    }

    @Override
    public void unlock(LockMode lockMode) {
        this.config.unlock(lockMode);
    }
}
