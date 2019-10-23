package productapi.category.hierarchy.model;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.hamcrest.CoreMatchers.equalTo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CategoryNodeTest {
    
    private static final Integer BIG = 10;
    private static final Integer BETWEEN = 6;
    private static final Integer SMALL = 3;
    
    @Mock private CategoryNode nodeA;
    @Mock private CategoryNode nodeB;
    @Mock private CategoryNode nodeC;
    
    private CategoryNode categoryNode;
    
    @Before
    public void setUp() {
        this.categoryNode = new CategoryNode();
    }

    @Test
    public void testGetNoChildren() {
        assertNull(categoryNode.getChildren());
    }
    
   @Test
   public void testGetChildrenOne() {
       specifyChild(nodeA, BIG);
       assertThat(categoryNode.getChildren().get(0), equalTo(nodeA));
   }

   @Test
   public void testGetChildrenTwo() {
       specifyChild(nodeA, BIG);
       specifyChild(nodeC, SMALL);
       assertThat(categoryNode.getChildren().get(0), equalTo(nodeA));
       assertThat(categoryNode.getChildren().get(1), equalTo(nodeC));
   }
   
   @Test
   public void testGetChildrenThreeFromBigToSmall() {
       specifyChild(nodeA, BIG);
       specifyChild(nodeB, BETWEEN);
       specifyChild(nodeC, SMALL);
       assertThat(categoryNode.getChildren().get(0), equalTo(nodeA));
       assertThat(categoryNode.getChildren().get(1), equalTo(nodeB));
       assertThat(categoryNode.getChildren().get(2), equalTo(nodeC));
   }
   
   @Test
   public void testGetChildrenThreeFromSmallToBig() {
       specifyChild(nodeC, SMALL);
       specifyChild(nodeB, BETWEEN);
       specifyChild(nodeA, BIG);
       assertThat(categoryNode.getChildren().get(0), equalTo(nodeA));
       assertThat(categoryNode.getChildren().get(1), equalTo(nodeB));
       assertThat(categoryNode.getChildren().get(2), equalTo(nodeC));
   }
   
   @Test
   public void testGetChildrenThreeFromSmallToBigToBetween() {
       specifyChild(nodeC, SMALL);       
       specifyChild(nodeA, BIG);
       specifyChild(nodeB, BETWEEN);
       assertThat(categoryNode.getChildren().get(0), equalTo(nodeA));
       assertThat(categoryNode.getChildren().get(1), equalTo(nodeB));
       assertThat(categoryNode.getChildren().get(2), equalTo(nodeC));
   }
   
   @Test
   public void testGetChildrenThreeFromBigToSmallToBetween() {
       specifyChild(nodeA, BIG);
       specifyChild(nodeC, SMALL);       
       specifyChild(nodeB, BETWEEN);
       assertThat(categoryNode.getChildren().get(0), equalTo(nodeA));
       assertThat(categoryNode.getChildren().get(1), equalTo(nodeB));
       assertThat(categoryNode.getChildren().get(2), equalTo(nodeC));
   }
   
   @Test
   public void testSetGetPathFromEmptyOrNull(){
       this.categoryNode.setPath("");
       assertThat(categoryNode.getPath(), equalTo(""));
       this.categoryNode.setPath(null);
       assertThat(categoryNode.getPath(), equalTo(""));
   }
   
   @Test
   public void testSetGetPathNotEmpty(){
       this.categoryNode.setPath("Path");
       assertThat(categoryNode.getPath(), equalTo("Path"));
   }
   
   @Test
   public void testGetPathOfChildWithParentAndEmptyPathItem(){
       CategoryNode child = new CategoryNode();
       child.setPathItem("pathItem");
       this.categoryNode.addChild(child);
       this.categoryNode.getChildren();
       assertThat(child.getPath(), equalTo("pathItem"));
   }
   
   @Test
   public void testGetPathOfChildWithParentAndPathItem(){
       this.categoryNode.setPathItem("root");
       CategoryNode child = new CategoryNode();
       child.setPathItem("pathItem");
       this.categoryNode.addChild(child);
       this.categoryNode.getChildren();
       assertThat(child.getPath(), equalTo("root/pathItem"));
   }

   private void specifyChild(CategoryNode child, Integer weigth) {
       doReturn(weigth).when(child).getWeigth();
       categoryNode.addChild(child);
   }

}
