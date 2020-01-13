import com.leyou.LeyouItemApplication;
import com.leyou.bo.SpuBo;
import com.leyou.item.service.GoodsService;
import com.leyou.pojo.Brand;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Time;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
@SpringBootTest(classes = LeyouItemApplication.class)
@RunWith(SpringRunner.class)
public class test {
    public static void main(String[] args) {
        Brand brand1 = new Brand(1,"aa","bb",'L');
        Brand brand2 = new Brand(2,"cc","dd",'X');
        Brand brand3 = new Brand(3,"ee","rr",'W');
        Brand brand4 = new Brand(3,"ee","rr",'W');
        Brand brand5 = new Brand(3,"ee","rr",'W');
        Brand brand6 = new Brand(3,"ee","rr",'W');
        Brand brand7 = new Brand(3,"ee","rr",'W');
        Brand brand8 = new Brand(3,"ee","rr",'W');
        Brand brand9 = new Brand(3,"ee","rr",'W');
        Brand brand10 = new Brand(3,"ee","rr",'W');
        List<Brand> brands = Arrays.asList(brand1, brand2, brand3,brand4,brand5,brand6,brand7,brand8,brand9,brand10);
        System.out.println(new DateTime());
        Brand brand11 = new Brand();
        brands.forEach(brand -> {

            brand4.setId(brand.getId());
            brand4.setImage(brand.getImage());
            brand4.setName(brand.getName());
            brand4.setLetter(brand.getLetter());
            System.out.println(brand4);

        });
        System.out.println(new DateTime());


    }
    @Autowired
    private GoodsService goodsService;
    @Test
    public void test2(){

        System.out.println(goodsService.querySkuBySpuId(2L));
    }

}
