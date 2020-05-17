import java.util.ArrayList;
import java.util.List;

/**
 * @Author Albert Piekielny
 */
public class CodeVector {

    private Pixel representative;
    private List<Pixel> nearestMembers;

    public CodeVector(Pixel representative) {
        this.representative = representative;
        this.nearestMembers = new ArrayList<>();
    }

    public void clearMember() {
        this.nearestMembers = new ArrayList<>();
    }

    public void addMember(Pixel pixel) {
        this.nearestMembers.add(pixel);
    }

    public Pixel getRepresentative() {
        return representative;
    }

    public List<Pixel> getNearestMembers() {
        return nearestMembers;
    }

    public void setRepresentative(Pixel pixel) {
        this.representative = pixel;
    }

    @Override
    public String toString() {
        return "CodeVector" + String.format("[%1.0f,%1.0f,%1.0f]", representative.getRed(),
                representative.getGreen(), representative.getBlue());
    }
}
