package Model;

public class ProductsModel {

    String marketName,productNo,page,mrp,innerPack,outerPack,qty;

    public ProductsModel(){

    }

    public ProductsModel(String marketName,String productNo,String page,String mrp,String innerPack,String outerPack){

        this.marketName = marketName;
        this.productNo = productNo;
        this.page = page;
        this.mrp = mrp;
        this.innerPack = innerPack;
        this.outerPack = outerPack;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getInnerPack() {
        return innerPack;
    }

    public void setInnerPack(String innerPack) {
        this.innerPack = innerPack;
    }

    public String getOuterPack() {
        return outerPack;
    }

    public void setOuterPack(String outerPack) {
        this.outerPack = outerPack;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }
}
