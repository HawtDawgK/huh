package nsfw.util;

import java.util.ArrayList;
import java.util.Objects;

public class LimitedSizeQueue<K> extends ArrayList<K> {

    private final int maxSize;

    public LimitedSizeQueue(int size){
        this.maxSize = size;
    }

    @Override
    public boolean add(K k){
        boolean r = super.add(k);
        if (size() > maxSize){
            removeRange(0, size() - maxSize);
        }
        return r;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LimitedSizeQueue<?> that = (LimitedSizeQueue<?>) o;
        return maxSize == that.maxSize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), maxSize);
    }
}
