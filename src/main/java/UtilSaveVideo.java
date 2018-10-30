import save.video.stream.ReadImageFromSteam;

public class UtilSaveVideo {
    public static void main(String a[]) throws Exception {
        String filename = "http://213.59.235.103:2224/ZPYTNYCUWPMZSD5CPZQL7OAQLGISRD5HSKCINSHZDABVRQWHCLH5GEA3ZDEI53II76XG565LQUTO3DYFAPXKFH3UBMMXT74ZIIJAB6A6GGPOJZSN76OWKIJN47G47BUURQHDZMFQ5FGNWB4MPPTVB36NKWB7T76PBOGPYX4DMYUFLA4ICJLUKDKZDRUG7HMLKN3WPNNXEGOUC/8e93851bde2659019038ffea462a4e2b-public";

        ReadImageFromSteam readImageFromSteam = new ReadImageFromSteam(filename, "1", 1_000);
        readImageFromSteam.start();
    }
}