package junit;

public class Tanxin {

	public static void main(String[] args) {
		int[] nums ={2,3,1,1,4};
		System.out.println(Tanxin.jump(nums));
	}

	public static int jump(int[] nums) {
		int reach = 0;// 全局最远可达位置
		int last = 0;// 上一步最远能到达位置
		int step = 0;// i需要超过上一步最远位置时加1
		for (int i = 0; i <= reach && i < nums.length; i++) {
			if (i > last) {
				step++;
				last = reach;
			}

			if (reach < nums[i] + i) {
				reach = nums[i] + i;
			}
		}
		return reach >= (nums.length - 1) ? step : 0;// 超过也算是到了终点
	}

}
