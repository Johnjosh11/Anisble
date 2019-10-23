package productapi.category.hierarchy.model;

import static org.mockito.Mockito.doReturn;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CategoryWeightComparatorTest {
    
    private static final Integer BIG = 10;
    private static final Integer SMALL = 3;
    
    private static final Integer BIGGER = 7;
    private static final Integer SMALLER = -7;
    private static final Integer EQUAL = 0;
    
    @Mock private CategoryNode nodeA;
    @Mock private CategoryNode nodeB;
    
    private CategoryWeightComparator comparator = new CategoryWeightComparator();

    @Test
    public void testCompareBiggerWeigth() {
        specifyWeight(nodeA, BIG);
        specifyWeight(nodeB, SMALL);
        assertThat(comparator.compare(nodeA, nodeB), equalTo(SMALLER));
    }
    
    @Test
    public void testCompareSmallerWeigth() {
        specifyWeight(nodeA, SMALL);
        specifyWeight(nodeB, BIG);
        assertThat(comparator.compare(nodeA, nodeB), equalTo(BIGGER));
    }
    
    @Test
    public void testCompareEqualsWeigth() {
        specifyWeight(nodeA, EQUAL);
        specifyWeight(nodeB, EQUAL);
        assertThat(comparator.compare(nodeA, nodeB), equalTo(EQUAL));
    }

    private void specifyWeight(CategoryNode categoryNode, Integer weigth) {
        doReturn(weigth).when(categoryNode).getWeigth();
    }
    
}
