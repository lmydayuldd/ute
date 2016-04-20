/**
 * 
 */
package at.sume.sampling;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Alexander Remesch
 *
 */
public class DistributionTest {
	private class TestRow {
		private byte classId;
		public long probability;

		public TestRow(byte classId, long probability) {
			this.classId = classId;
			this.probability = probability;
		}
		public byte getClassId() {
			return classId;
		}
		public void setClassId(byte classId) {
			this.classId = classId;
		}
		public long getProbability() {
			return probability;
		}
		public void setProbability(long probability) {
			this.probability = probability;
		}
	}
	private Distribution<TestRow> testDistribution;
	private List<TestRow> baseData;
	
	private static final int TOTAL_SAMPLES = 1000;
	
	@Before
	public void setUp() {
		baseData = new ArrayList<TestRow>() {{
			add(new TestRow((byte) 1, 30));
			add(new TestRow((byte) 2, 30));
			add(new TestRow((byte) 3, 40));
		}};
		try {
			testDistribution = new Distribution<TestRow>(baseData, "probability");
		} catch (SecurityException | IllegalArgumentException | NoSuchFieldException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Test method for {@link at.sume.sampling.ExactDistribution#randomSample()}.
	 */
	@Test
	public void testRandomSample() {
		Map<Byte,Long> resultCount = new HashMap<Byte,Long>();
		
		// Sample all TestRows & count them
		for (int i = 0; i != TOTAL_SAMPLES; i++) {
			int sampleResult = testDistribution.randomSample();
			assertTrue("sampleResult not in byte range (" + sampleResult + ")", sampleResult >= 0 && sampleResult <= 255);
			Long currentCount = resultCount.get((byte)sampleResult);
			if (currentCount == null) currentCount = 0L;
			resultCount.put((byte) sampleResult, currentCount + 1);
		}
		
		long total = 0;
		for(TestRow b : baseData) {
			long desired = b.getProbability();
			Long actual = resultCount.get((byte)(b.getClassId() - 1));
			if (actual == null) actual = 0L;
			System.out.println(b.getClassId() + ": desired = " + desired + ", actual = " + actual);
			total += actual;
		}
		System.out.println("Total = " + total);
	}
}
