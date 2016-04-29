package at.sume.dm.migration;

public class MigrationsPerAgeSex implements Comparable<MigrationsPerAgeSex> {
	private int id;
	private byte ageGroupId;
	private byte sex;
	// public is necessary for use of Distribution/ExactDistribution classes
	public long share;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the ageGroupId
	 */
	public byte getAgeGroup20Id() {
		return ageGroupId;
	}

	/**
	 * @param ageGroupId
	 *            the ageGroupId to set
	 */
	public void setAgeGroup20Id(byte ageGroupId) {
		this.ageGroupId = ageGroupId;
	}

	/**
		 * @return the sex
		 */
		public byte getSex() {
			return sex;
		}

	/**
	 * @param sex
	 *            the sex to set
	 */
	public void setSex(byte sex) {
		this.sex = sex;
	}

	/**
	 * @return the share
	 */
	public long getShare() {
		return share;
	}

	/**
	 * @param share
	 *            the share to set
	 */
	public void setShare(long share) {
		this.share = share;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(MigrationsPerAgeSex o) {
		return ((Integer) id).compareTo(o.id);
	}
}
