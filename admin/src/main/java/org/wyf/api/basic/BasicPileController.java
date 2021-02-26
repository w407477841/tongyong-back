package org.wyf.api.basic;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.wyf.basic.entity.BasicPile;
import org.wyf.basic.entity.BasicStation;
import org.wyf.basic.service.IBasicPileService;
import org.wyf.common.constant.OperationEnum;
import org.wyf.common.controller.BaseController;
import org.wyf.common.dto.DataDTO;
import org.wyf.common.dto.RequestDTO;
import org.wyf.common.dto.ResultDTO;
import org.wyf.system.model.Role;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yaoyun
 * @since 2019-12-09
 */
@RestController
@RequestMapping("/basic/basic-pile")
public class BasicPileController extends BaseController<BasicPile,IBasicPileService> {


@Override
public String insertRole() {
        return "basic:basicpile:insert";
        }

@Override
public String updateRole() {
        return "basic:basicpile:update";
        }

@Override
public String deleteRole() {
        return "basic:basicpile:delete";
        }

@Override
public String viewRole() {
        return "basic:basicpile:view";
        }

        /**
         * 获取桩列表
         * @param t
         * @return
         */
   /*     @PostMapping("selectPileList")
        public ResultDTO<DataDTO<List<BasicPile>>> selectPileList(@RequestBody RequestDTO<BasicPile> t) {
                hasPermission(viewRole());
                String tableName =  t.getBody().getClass().getAnnotation(TableName.class).value();
                String createUserName = StrUtil.format("(select name from t_sys_user where t_sys_user.id = t_basic_pile.create_user ) as createUserName," +
                        "(select name from t_basic_station where t_basic_station.id = t_basic_pile.station_id ) as stationName ",tableName);
                Page<BasicPile> page = new Page<>(t.getPageNum(), t.getPageSize());
                QueryWrapper<BasicPile> queryWrapper = new QueryWrapper<>();
                queryWrapper.select("*",createUserName);
                if(t.getColumns()!=null&&t.getColumns().size()>0&& StrUtil.isNotBlank(t.getKey())){
                        // 组装 like 语句
                        queryWrapper.and(tQueryWrapper -> {
                                t.getColumns().forEach(item->{
                                        tQueryWrapper.or().like(item,t.getKey());

                                });
                                return tQueryWrapper;
                        });
                }
                queryWrapper.orderByDesc("create_time");

                List<BasicPile> ts = service.page(page,queryWrapper).getRecords();

                return new ResultDTO<DataDTO<List<BasicPile>>>(true, DataDTO.factory(ts, page.getTotal()));
        }*/


        /**
         * 获取桩列表
         * @param requestDTO
         * @return
         */
        @PostMapping("selectPileList")
        public ResultDTO<DataDTO<List<BasicPile>>> selectPileList(@RequestBody RequestDTO<BasicPile> requestDTO) {
                try {
                        Page<BasicPile> page = new Page<>(requestDTO.getPageNum(),requestDTO.getPageSize());
                        List<BasicPile> list = service.selectPileInfo(page,requestDTO);
                        return new ResultDTO<>(true,DataDTO.factory(list,page.getTotal()));
                }catch (Exception e) {
                        e.printStackTrace();
                }
                return new ResultDTO<>(false);
        }

        /**
         * 获取站名
         * @return
         */
        @PostMapping("/getStationInfo")
        public ResultDTO<List<BasicStation>> getStationInfo() {
                try {
                        List<BasicStation> stations = service.getStationInfo();
                        return new ResultDTO<>(true,stations);
                }catch (Exception e) {
                        e.printStackTrace();
                }
                return new ResultDTO<>(false);
        }

        /**
         * 新增
         * @param requestDTO
         * @return
         */
        @PostMapping("insertBasicPile")
        public  ResultDTO insertBasicPile(@RequestBody RequestDTO<BasicPile> requestDTO){
                hasPermission(this.insertRole());
                try {
                        return  service.insertBasicPile(requestDTO.getBody());
                }catch (Exception ex){
                        ex.printStackTrace();
                }
                return ResultDTO.resultFactory(OperationEnum.INSERT_ERROR);
        }

        /**
         * 修改
         * @param requestDTO
         * @return
         */
        @PostMapping("updateBasicPile")
        public  ResultDTO updateBasicPile(@RequestBody RequestDTO<BasicPile> requestDTO){
                hasPermission(this.updateRole());
                try {
                        return  service.updateBasicPile(requestDTO.getBody());
                }catch (Exception ex){
                        ex.printStackTrace();
                }
                return ResultDTO.resultFactory(OperationEnum.UPDATE_ERROR);
        }

        /**
         *  转换设备号
          * @param requestDTO
         * @return
         */
        @PostMapping("transformSn")
        public ResultDTO transformSn(@RequestBody RequestDTO<BasicPile> requestDTO){

                String sn =  requestDTO.getBody().getDeviceNo();

                String [] snArrays = new String[5];
                if(sn.length()==11){
                       snArrays[0] = sn.substring(1,3);
                        snArrays[1] = sn.substring(3,5);
                        snArrays[2] = sn.substring(5,7);
                        snArrays[3] = sn.substring(7,9);
                        snArrays[4] = sn.substring(9,11);
                }

              String deviceNo =   StrUtil.fillBefore(Integer.toHexString(Integer.parseInt(snArrays[0])),'0',2)+

                         StrUtil.fillBefore(Integer.toHexString(Integer.parseInt(snArrays[1])),'0',2)+
                         StrUtil.fillBefore(Integer.toHexString(Integer.parseInt(snArrays[2])),'0',2)+
                         StrUtil.fillBefore(Integer.toHexString(Integer.parseInt(snArrays[3])),'0',2)+
                         StrUtil.fillBefore(Integer.toHexString(Integer.parseInt(snArrays[4])+128),'0',2);

                return new ResultDTO(true,deviceNo.toUpperCase());



        }

}









