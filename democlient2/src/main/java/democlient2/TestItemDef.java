package democlient2;
public class TestItemDef {
	public static class FIBER_CHARACTERISTICS{
		public final static Long OPTICAL_FIBER_LENGTH__KM = 1L;
		public final static Long TOTAL_LINK_LOSS__DB = 2L;
		public final static Long OPTICAL_RETURN_LOSS__DB = 4L;
		public final static Long WORST_REFLECTION = 7L;
		public final static Long CONNECTION_COUNT = 13L;
		public final static Long FUSION_COUNT = 14L;
	}
	public static class FIBER_END_FACE_CHARACTERRISTICS{
		public final static Long CORE_SCRACHES = 9L;
		public final static Long CORE_DEFECTS = 21L;
		public final static Long CLADDING_SCRACHES = 22L;
		public final static Long CLADDING_DEFECTS = 23L;
		public final static Long ADHESIVE_SCRACHES = 24L;
		public final static Long ADHESIVE_DEFECTS = 25L;
		public final static Long CONTACT_SCRATCHES = 26L;
		public final static Long CONTACT_DEFECTS = 27L;
		public final static Long CORE_SCRACHES_MPO12 = 34L;
		public final static Long CORE_DEFECTS_MPO12 = 35L;
	}
	public static class RECEIVE_SIGNAL_CHARACTERRISTICS{
		public final static Long RECEIVE_POWER__DBM = 5L;
	}
	public static class TRANSMIT_SIGNAL_CHARACTERISTICS{
		public final static Long CHANNEL_POWER_FLATNESS__DB = 8L;
		public final static Long TRANSMIT_OPTICAL_POWER__DBM = 10L;
		public final static Long CHANNEL_COUNT = 15L;
		public final static Long AVERAGE_OSNR__DB = 16L;
		public final static Long WORST_OSNR__DB = 17L;
		public final static Long GAIN_SLOPE__DB = 18L;
	}
}
