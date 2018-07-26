package merge.aaa.util;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
@Component
public class RedisUtils {
	
	@Resource(name = "redisTemplate")
	private ValueOperations<String, String> valueOps;

	@Resource(name = "redisTemplate")
	private HashOperations<String, String, String> hashOps;

	@Resource(name = "redisTemplate")
	private ZSetOperations<String, String> zOps;

	@Resource(name = "redisTemplate")
	private ListOperations<String, String> listOps;
	
	public Long lPush(String key, String userId){
		listOps.remove(key, 0, userId);
		return listOps.leftPush(key, userId);
	}
	public Long lSize(String key){
		return listOps.size(key);
	}
	public void lDelete(String key, String value){
		listOps.remove(key, 0, value);	
	}
	public List<String> lGetPage(String key, long start, long end){
		return listOps.range(key, start, end);
	}
	
	public List<String> getSn(String key, long start, long end){
		return listOps.range(key, start, end);
	}
	
	public void set(String key, String value) {
		valueOps.set(key, value);
	}
	
	public void set(String key, String value, long timeout) {
		valueOps.set(key, value, timeout, TimeUnit.SECONDS);
	}
	
	public String get(String key) {
		return valueOps.get(key);
	}
	public void hSet(String key, String hashKey, String value) {
		hashOps.put(key, hashKey, value);
	}
	public String hGet(String key, String hashKey) {
		return hashOps.get(key, hashKey);
	}
	public Long hSize(String key) {
		return hashOps.size(key);
	}
	public Boolean hHasKey(String key, String hashKey) {
		return hashOps.hasKey(key, hashKey);
	}
	public Set<String> hGetKeys(String key) {
		return hashOps.keys(key);
	}
	public long hDelete(String key, String hashKey){
		return hashOps.delete(key, hashKey);
	}
	public Map<String, String> getHashData(String key){
		return hashOps.entries(key);
	}
	
	public Boolean zSet(String key, String value, double score) {
		return zOps.add(key, value, score);
	}
	public Long zSize(String key) {
		return zOps.size(key);
	}
	public Set<String> zReverseRangeByScore(String key, double min, double max, long offset, long count) {
		return zOps.reverseRangeByScore(key, min, max, offset, count);
	}
	public Set<String> zRange(String key, long start, long end) {
		return zOps.range(key, start, end);
	}
	public Long zDelete(String key, long start, long end) {
		return zOps.removeRange(key, start, end);
	}
	public Long zRemoveRangeByScore(String key, long start, long end) {
		return zOps.removeRangeByScore(key, start, end);
	}
	public Long zDelete(String key, String hashKey) {
		return zOps.remove(key, hashKey);
	}
	public Set<String> zRangeByScore(String key, double min, double max) {
		return zOps.rangeByScore(key, min, max);
	}
}
