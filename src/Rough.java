
public class Rough {

	static String decrypt(String word) {
		String ans = "";
		if(word.length() == 0) {
			return ans;
		}
		ans += get(word.charAt(0)-1);
		int n = word.length();
		int sum = 0;
		for(int i=1;i<n;++i) {
			sum += ans.charAt(i - 1);
			ans += get(word.charAt(i) - 1 - sum);
		}
		return ans;
	}
	
	static String get(int num) {
		int diff = 97-num;
		if(diff < 0) {
			return Character.toString((char)num);
		}
		int cnt = diff / 26;
		if(diff % 26 != 0) {
			++cnt;
		}
		return Character.toString((char)(cnt * 26 + num));
	}

	public static void main(String[] args) {
		System.out.println(decrypt("flgxswdliefy"));
	}
}
