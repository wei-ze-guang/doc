package pattern.build;

import org.junit.Test;

public class Product {

    @Test
    public void test(){
        Product p = new Builder()
                .partA("sss")
                .partB("partB")
                .build();
    }
    // 一些私有属性
    private String partA;
    private String partB;
    private int partC;

    public static class Builder {
        private String partA;
        private String partB;
        private int partC;
        public Builder partA(String partA) {
            this.partA = partA;
            return this;
        }

        public Builder partB(String partB) {
            this.partB = partB;
            return this;
        }
        public Builder partC(int partC) {
            this.partC = partC;
            return this;
        }
        public Product build() {
            Product product = new Product();
            product.partA = partA;
            product.partB = partB;
            product.partC = partC;
            return product;
        }
    }
}
