package midterm;
import java.awt.*;
import simView.*;

public class efp extends ViewableDigraph {
	// constructor
	public efp() {
		super("efp");
		// enemy, ef 선언
		ViewableAtomic enemy = new enemy("enemy");
		ViewableDigraph ef = new ef("ef", 20, 1000);
		
		// enemy, ef 표시
		add(enemy);
		add(ef);
		
		// port간 연결
		addCoupling(ef, "out", enemy, "in");  // ef의 out -> enemy의 in
		addCoupling(enemy, "out", ef, "in");  // enemy의 out -> ef의 in
	}
	
    /**
     * Automatically generated by the SimView program.
     * Do not edit this manually, as such changes will get overwritten.
     */
    public void layoutForSimView()
    {
        preferredSize = new Dimension(980, 644);
        ((ViewableComponent)withName("enemy")).setPreferredLocation(new Point(490, 501));
        ((ViewableComponent)withName("ef")).setPreferredLocation(new Point(273, 111));
    }
 }
