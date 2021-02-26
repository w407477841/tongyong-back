package org.wyf.api;

import org.wyf.common.Const;
import org.wyf.common.dto.RequestDTO;
import org.wyf.common.dto.ResultDTO;
import org.wyf.dto.DataDTO;
import org.wyf.dto.DataDTO2;
import org.wyf.dto.DeviceDTO;
import org.wyf.server.BaseServer;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wyf.common.Const;
import org.wyf.common.dto.RequestDTO;
import org.wyf.common.dto.ResultDTO;
import org.wyf.dto.DataDTO;
import org.wyf.dto.DataDTO2;
import org.wyf.server.BaseServer;

@RestController
@RequestMapping("/remote")
public class RemoteAPI {
    @RequestMapping("action" )
    public ResultDTO action(@RequestBody RequestDTO<DeviceDTO> requestDTO){
        DeviceDTO body = requestDTO.getBody();
        Channel channel = Const.CHANNEL_MAP.get(body.getDeviceNo());
        if(channel==null){
            return new ResultDTO<>(false,"","设备未上线");
        }
        try {
        BaseServer.services.stream().filter(p->
                p.supported(body.getCmd())
        ).forEach(item->{

                item.remoteProcess(body);

        });
        }catch (Exception e){
            return new ResultDTO<String>(false,"",e.getMessage());
        }
        DataDTO2 dto2 =new DataDTO2(body.getCmd(),body.getPlug(),body.getData(),null);
        String data = dto2.toData();
        DataDTO dataDTO =new DataDTO("00AB",body.getDeviceNo(),data,null);
        data = dataDTO.toData();
        channel.writeAndFlush(Unpooled.copiedBuffer(Const.reTranslation(data)));

        try {
            BaseServer.services.stream().filter(p->
                    p.supported(body.getCmd())
            ).forEach(item->{

                item.update(body);

            });
        }catch (Exception e){
            return new ResultDTO<String>(false,"",e.getMessage());
        }


        return        ResultDTO.success("成功");
    }



}
