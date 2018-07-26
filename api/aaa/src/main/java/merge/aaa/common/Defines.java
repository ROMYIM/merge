package merge.aaa.common;

public class Defines {

	public final static int tokenexpires=60 * 60;
	public final static String AuthInfoExpireTime="1800";
	public final static String EPGHeartTime="1200";
	//iptvuser key=userid,value对应ptv_user表值
	public final static String IPTVUSER_TABLE = "IPTVUSER_TABLE";
	//iptvuser online，登录后，记录用户在线信息，key=userid，value用户信息收集
	public final static String IPTVUSERONLINE_TABLE = "IPTVUSERONLINE_TABLE";
	//usertoken，登录后生成token，key=usertoken，value=userid，与iptvuser online对应
	public final static String USERTOKEN_TABLE = "USERTOKEN_TABLE";
	//usertoken过期时间，key=usertoken
	public final static String USERTOKEN_TIMEOUT_TABLE = "USERTOKEN_TIMEOUT_TABLE";
	//platform，平台id，key=3id，value保存cpspid、operator、epggroupnum
	public final static String PLATFORM_TABLE = "PLATFORM_TABLE";
	//stbid，key=userid，value 3id
	public final static String STBID_TABLE = "STBID_TABLE";
	//CmsDomain，key=userid，用户对于的列表信息
	public final static String CMSDOMAIN_TABLE = "CMSDOMAIN_TABLE";
}
