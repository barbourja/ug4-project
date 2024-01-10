package generic;

import java.util.Collection;

public interface Algorithm<T extends Collection<T>> {
    T execute(T input);
}
