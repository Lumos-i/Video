package com.video.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.video.config.JedisConfig;
import com.video.controller.webSocket.MyWebSocket;
import com.video.dto.*;
import com.video.mapper.*;
import com.video.pojo.*;
import com.video.pojo.Collections;
import com.video.service.UserService;
import com.video.utils.*;
import com.video.vo.UserInfoVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @ClassName: UserServiceImpl
 * @author: 赵容庆
 * @date: 2022年09月22日 17:16
 * @Description: TODO
 */

@Service
@Slf4j
@Transactional
@SuppressWarnings("rawtypes")
public class UserServiceImpl implements UserService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private AdvertisingMapper advertisingMapper;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CosUtil cosUtil;
    @Autowired
    private FeedBackMapper feedBackMapper;
    @Autowired
    private HistoryMapper historyMapper;
    @Autowired
    private CollectionsMapper collectionsMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private FollowMapper followMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private CommentLoveMapper commentLoveMapper;
    @Autowired
    private VipPriceMapper vipPriceMapper;
    @Autowired
    private RemindMapper remindMapper;
    @Autowired
    private CommentReportMapper commentReportMapper;
    @Autowired
    private VideosAddressMapper videosAddressMapper;
    @Autowired
    private TypeMapper typeMapper;
    @Resource
    private JedisConfig jedisConfig;

    /**
     *
     * @param username
     * @param password
     */
    @Override
    public BaseResponse login(String username, String password) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return BaseResponse.nullValue("所填内容不能为空");
        }
        log.info(username+","+password);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if (Objects.isNull(authenticate)) {
            throw new RuntimeException("用户名或密码错误");
        }
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        log.info(loginUser + "");
        Integer power = loginUser.getUser().getPower();
        String jwt = JwtUtils.getJwtToken(username, power + "", loginUser.getUser().getId());
        //authenticate存入redis
        redisCache.setCacheObject("login:" + username, loginUser, 60 * 24, TimeUnit.MINUTES);
        User user = loginUser.getUser();
        //对比封禁日期
        LocalDateTime time=user.getCloseTime();
        int num = time.compareTo(LocalDateTime.now());
        if(num>0){//仍处于封禁状态
            return BaseResponse.success("该账号处于封禁期间",time);
        }
        redisCache.setCacheObject("login:" + user.getId(), user, 60 * 24, TimeUnit.MINUTES);
        Map<String, String> map = new HashMap<>();
        map.put("token", jwt);
        log.info(username + "已登录");
        return BaseResponse.success(map);
    }

    /**
     * 发送验证码
     *
     * @param email
     * @return
     */
    @Override
    public BaseResponse sendCode(String email) {
        if (StringUtils.isEmpty(email.trim())) {
            return BaseResponse.nullValue("数据不能为空");
        }
        if (!RegExpUtil.matchEmail(email)) {
            return BaseResponse.error("邮箱格式错误");
        }
        if (redisCache.getCacheObject("email" + email) != null) {
            return BaseResponse.success("您的操作过于频繁，请再30s后再重试");
        }

        int code = SendMailVerify.MailVerify(email);
        redisCache.setCacheObject(email, code, 5, TimeUnit.MINUTES);

        redisCache.setCacheObject("email" + email, email, 30, TimeUnit.SECONDS);
        return BaseResponse.success("已发送", code);
    }

    /**
     * 修改密码
     *
     * @param email
     * @param code
     * @param password
     * @return
     */
    @Override
    public BaseResponse updatePassword(String email, String code, String password) {
        if (!RegExpUtil.matchEmail(email)) {
            return BaseResponse.error("邮箱格式不正确");
        }
        if (!RegExpUtil.matchPassword(password)) {
            return BaseResponse.error("密码须含至少一个字母且密码长度位6到16位");
        }
        if (StringUtils.isEmpty(redisCache.getCacheObject(email))) {
            return BaseResponse.error("邮箱错误或验证码已失效");
        }
        if (code.equals(redisCache.getCacheObject(email).toString())) {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getEmail, email);
            User user = new User();
            user.setPassword(new BCryptPasswordEncoder().encode(password));
            userMapper.update(user, wrapper);
            return BaseResponse.success("已修改");
        } else {
            return BaseResponse.error("验证码错误");
        }
    }

    @Override
    public BaseResponse logOut() {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser principal = (LoginUser) authenticationToken.getPrincipal();
        String username = principal.getUsername();
        redisCache.deleteObject("login:" + username);
        return BaseResponse.success("已退出");
    }

    @Override
    public BaseResponse updatePower(Integer id, String introduction, String phone) {
        if (id == null || StringUtils.isEmpty(introduction) || StringUtils.isEmpty(phone)) {
            return BaseResponse.nullValue("数据不能为空");
        }
        User user = new User();
        user.setId(id);
        user.setIntroduction(introduction);
        user.setPhone(phone);
        user.setUploadPower(2);
        return userMapper.updateById(user) > 0 ? BaseResponse.success("已发送申请") : BaseResponse.error("服务器错误");
    }

    /**
     * 注册
     *
     * @param username
     * @param password
     * @param email
     */
    @PostMapping("/register")
    @ApiModelProperty("注册")
    @Override
    public BaseResponse register(String username, String password, String email, String code) {
        log.info(email + "," + code);
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password) || StringUtils.isEmpty(email) || StringUtils.isEmpty(code)) {
            return BaseResponse.nullValue("数据不能为空");
        }
        if (StringUtils.isEmpty(redisCache.getCacheObject(email))) {
            return BaseResponse.error("验证码已失效");
        }
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username).or().eq(User::getEmail, email);
        User user1 = userMapper.selectOne(wrapper);
        if (!Objects.isNull(user1)) {
            return BaseResponse.error("账号或邮箱已存在");
        }
        if (!RegExpUtil.matchUsername(username)) {
            return BaseResponse.error("账号须为9到12位数字");
        }
        if (!RegExpUtil.matchPassword(password)) {
            return BaseResponse.error("密码须含至少一个字母且密码长度位6到16位");
        }
        if (code.equals(redisCache.getCacheObject(email).toString()) || (redisCache.getCacheObject(email).toString()).equals(code)) {
            if (RegExpUtil.matchEmail(email)) {
                User user = new User();
                user.setUsername(username);
                user.setPassword(new BCryptPasswordEncoder().encode(password));
                user.setName("用户" + (new Random().nextInt(8999999) + 1000000));
                user.setEmail(email);
                user.setVipOutTime(LocalDateTime.now());
                user.setCloseTime(LocalDateTime.now());
                return userMapper.insert(user) > 0 ? BaseResponse.success("注册成功") : BaseResponse.error("服务器错误");
            } else {
                return BaseResponse.error("邮箱格式错误");
            }
        } else {
            return BaseResponse.error("验证码错误");
        }
    }

    /***
     * 上传头像
     * @param file
     */
    @Override
    public BaseResponse uploadHeadshot(MultipartFile file) {
        if (file.isEmpty()) {
            return BaseResponse.error("上传头像为空");
        }
        if (!FileTypeUtil.isImg(file)) {
            return BaseResponse.error("请上传图片做为头像！");
        }
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = loginUser.getUser();
        Integer id = user.getId();
        cosUtil.deleteObject(user.getPhoto(), 0);
        String headshotUrl = cosUtil.upload(file, 0);
        if ("格式不正确！".equals(headshotUrl)) {
            return BaseResponse.error(headshotUrl);
        }
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, id).set(User::getPhoto, headshotUrl);
        int result = userMapper.update(null, wrapper);
        if (result == 1) {
            user.setPhoto(headshotUrl);
            redisCache.setCacheObject("login:" + id, user, 60 * 24, TimeUnit.MINUTES);
            return BaseResponse.success("上传成功！", headshotUrl);
        }
        return BaseResponse.error("上传失败！");
    }

    @Override
    public BaseResponse updateUserMessage(UserInfoVo userInfoVo) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer id = loginUser.getUser().getId();
        if (StringUtils.isEmpty(userInfoVo.getSex()) && StringUtils.isEmpty(userInfoVo.getName()) && StringUtils.isEmpty(userInfoVo.getBirth()) && StringUtils.isEmpty(userInfoVo.getAddress())) {
            return BaseResponse.error("请传入数据！");
        }
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, id).set(!StringUtils.isEmpty(userInfoVo.getSex()), User::getSex, userInfoVo.getSex())
                .set(!StringUtils.isEmpty(userInfoVo.getName()), User::getName, userInfoVo.getName())
                .set(!StringUtils.isEmpty(userInfoVo.getBirth()), User::getBirth, userInfoVo.getBirth())
                .set(!StringUtils.isEmpty(userInfoVo.getAddress()), User::getAddress, userInfoVo.getAddress());
        if (userMapper.update(null, wrapper) == 1) {
            User user = redisCache.getCacheObject("login:" + id);
            if (!StringUtils.isEmpty(userInfoVo.getSex())) {
                user.setSex(userInfoVo.getSex());
            }
            if (!StringUtils.isEmpty(userInfoVo.getName())) {
                user.setName(userInfoVo.getName());
            }
            if (!StringUtils.isEmpty(userInfoVo.getBirth())) {
                user.setBirth(userInfoVo.getBirth());
            }
            if (!StringUtils.isEmpty(userInfoVo.getAddress())) {
                user.setAddress(userInfoVo.getAddress());
            }
            redisCache.setCacheObject("login:" + id, user, 60 * 24, TimeUnit.MINUTES);
            return BaseResponse.success("修改成功！", user);
        } else {
            return BaseResponse.error("修改失败！");
        }
    }

    @Override
    public BaseResponse getUserMessage(HttpServletRequest request) {
        User user;
        Integer id;
        try {
            id = JwtUtils.getMemberIdByJwtToken(request);
            user = (User) redisCache.getCacheObject("login:" + id);
        } catch (Exception e) {
            return BaseResponse.error("获取失败！");
        }
        if (user != null) {
            return BaseResponse.success("获取成功！", user);
        } else {
            User user1 = userMapper.selectById(id);
            redisCache.setCacheObject("login:" + id, user1, 60 * 24, TimeUnit.MINUTES);
            return BaseResponse.success("获取成功！", user1);
        }
    }

    @Override
    public BaseResponse addFeedBack(HttpServletRequest request, String context, String telephone) {
        if (StringUtils.isEmpty(context)) {
            return BaseResponse.nullValue("数据不能为空");
        }
        if (StringUtils.isEmpty(telephone)) {
            return BaseResponse.nullValue("联系方式不能为空！");
        }
        if (!RegExpUtil.matchesTelephone(telephone)) {
            return BaseResponse.error("电话格式不正确！");
        }

        Integer id = JwtUtils.getMemberIdByJwtToken(request);
        FeedBack feedBack = new FeedBack();
        feedBack.setUserId(id);
        feedBack.setContent(context);
        feedBack.setTelephone(telephone);
        feedBack.setFeedBackTime(LocalDateTime.now());
        return feedBackMapper.insert(feedBack) > 0 ? BaseResponse.success("已添加反馈") : BaseResponse.error("服务器错误");
    }

    @Override
    public BaseResponse getFeedBackHistory(HttpServletRequest request, Integer nodePage, Integer pageSize) {
        Integer id = null;
        try {
            id = JwtUtils.getMemberIdByJwtToken(request);
        }catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (nodePage <= 0) {
            nodePage = 1;
        }
        if (pageSize <= 0 || pageSize >= 15) {
            pageSize = 10;
        }
        Page<FeedBack> page = new Page<>(nodePage, pageSize);
        LambdaQueryWrapper<FeedBack> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeedBack::getUserId, id);
        feedBackMapper.selectPage(page, wrapper);
        return BaseResponse.success(page);
    }

    @Override
    public BaseResponse getCollectionsList(Integer userId) {
        System.out.println(userId);
        if (userId == null || userId <= 0) {
            return BaseResponse.error("id不合法！");
        }
        System.out.println(userId);
        List<ShowVideoDto> list = userMapper.getCollectionsList(userId);
        System.out.println(list);
        return null;
    }

    @Override
    public BaseResponse sign() {
        LoginUser loginUser = (LoginUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = loginUser.getUser();
        //获取用户登录id
        Integer id = user.getId();
//        Integer id = JwtUtils.getMemberIdByJwtToken(request);
        user = (User) redisCache.getCacheObject("login:" + id);
        //获取日期
        LocalDateTime now = LocalDateTime.now();
        //拼接key
        String keySuffix = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        String key = "sign" + id + keySuffix;
        //获取这是本月第几天
        int day = now.getDayOfMonth();
        redisCache.sign(key, day - 1, true);
        int giveIntegral = 0;
        int integral = user.getIntegral();
        giveIntegral = (int) (Math.random() * 5 + 1);
        log.info((giveIntegral + 1) + "");
        User users = new User();
        users.setId(id);
        users.setVip(user.getVip());
        users.setIntegral(integral + giveIntegral + 1);
        int i = userMapper.updateById(users);
        log.info(i + "=====" + "integral + giveIntegral + 1");
        redisCache.deleteObject("login:" + id);
        return BaseResponse.success("已签到,获得" + (giveIntegral + 1) + "积分");
    }

    @Override
    public BaseResponse getSign(HttpServletRequest request) {
        // 1.获取当前登录用户
        Integer userId = JwtUtils.getMemberIdByJwtToken(request);
        // 2.获取日期
        LocalDateTime now = LocalDateTime.now();
        // 3.拼接key
        String keySuffix = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        String key = "sign" + userId + keySuffix;
        // 4.获取今天是本月的第几天
        int dayOfMonth = now.getDayOfMonth();
        // 5.获取本月截止今天为止的所有的签到记录，返回的是一个十进制的数字
        List<Long> result = redisCache.result(key, dayOfMonth, 0);
        //总签到次数
        Long aLong = redisCache.bigCount(key);
        log.info(aLong + "");
        //所有的签到数据
        List<String> resultList = new ArrayList<>();
        List<Long> bitFieldList = redisCache.getBitMap(key, dayOfMonth);
        if (bitFieldList != null && bitFieldList.size() > 0) {
            long valueDec = bitFieldList.get(0) != null ? bitFieldList.get(0) : 0;
            for (int i = dayOfMonth; i > 0; i--) {
                LocalDate tempDayOfMonth = LocalDate.now().withDayOfMonth(i);
                if (valueDec >> 1 << 1 != valueDec) {
                    resultList.add(tempDayOfMonth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                }
                valueDec >>= 1;
            }
        }
        log.info(resultList + "");
        if (result == null || result.isEmpty()) {
            // 没有任何签到结果
            return BaseResponse.success("没有签到记录");
        }
        Long num = result.get(0);
        if (num == null || num == 0) {
            return BaseResponse.success("没有签到记录");
        }
        //统计最后于一次连续签到次数
        // 6.循环遍历
        int count = 0;
        while (true) {
            // 6.1.让这个数字与1做与运算，得到数字的最后一个bit位  // 判断这个bit位是否为0
            if ((num & 1) == 0) {
                // 如果为0，说明未签到，结束
                break;
            } else {
                // 如果不为0，说明已签到，计数器+1
                count++;
            }
            // 把数字右移一位，抛弃最后一个bit位，继续下一个bit位
            num >>>= 1;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("signDate", resultList);
        map.put("continuousDate", count);
        map.put("allSign", aLong);
        return BaseResponse.success(map);
    }

    @Override
    public BaseResponse history(HttpServletRequest request, Integer nodePage, Integer pageSize) {
        Integer id = JwtUtils.getMemberIdByJwtToken(request);
        Page<HistoryDto> page = new Page<>(nodePage, pageSize);
        Page<HistoryDto> page1 = userMapper.history(page, id);
        return page1.getSize() == 0 ? BaseResponse.success("没有记录") : BaseResponse.success(page1);
    }

    @Override
    public BaseResponse addHistory(HttpServletRequest request, Integer videoId, Integer videoAddressId, String progress) {
        int insert = 0;
        int update = 0;
        Integer id = JwtUtils.getMemberIdByJwtToken(request);
        if (id == null || videoId == null || videoAddressId == null) {
            return BaseResponse.nullValue("数据不能为空");
        }
        LambdaQueryWrapper<History> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(History::getUserId, id).eq(History::getVideoId, videoId);

        History history = historyMapper.selectOne(wrapper);
        if (Objects.isNull(history)) {
            insert = historyMapper.insert(new History(null, id, videoId, videoAddressId, progress, null));
        } else {
            History newHistory = new History();
            newHistory.setId(history.getId());
            newHistory.setVideoAddressId(videoAddressId);
            newHistory.setProgress(progress);
            update = historyMapper.updateById(newHistory);
        }
        return insert > 0 || update > 0 ? BaseResponse.success("已添加") : BaseResponse.error("服务器错误");
    }

    @Override
    public BaseResponse deleteHistory(List<Integer> ids) {
        if (ids.isEmpty()) {
            return BaseResponse.nullValue("数据不能为空");
        }
        int i = historyMapper.deleteBatchIds(ids);
        return i > 0 ? BaseResponse.success("已删除") : BaseResponse.error("服务器错误");
    }

    @Override
    public BaseResponse getCollections(HttpServletRequest request, Integer nodePage, Integer pageSize) {
        Integer id = JwtUtils.getMemberIdByJwtToken(request);
        if (id == null || nodePage == null || pageSize == null) {
            return BaseResponse.nullValue("数据不能为空");
        }


        Page<CollectionInfoDto> page = new Page<>(nodePage, pageSize);
        Page<CollectionInfoDto> collections = collectionsMapper.getCollections(id, page);
        return collections.getSize() == 0 ? BaseResponse.success("没有收藏") : BaseResponse.success(collections);
    }

    @Override
    public BaseResponse deleteAllHistory(HttpServletRequest request) {
        Integer id = JwtUtils.getMemberIdByJwtToken(request);
        LambdaQueryWrapper<History> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(History::getUserId, id);
        return historyMapper.delete(wrapper) > 0 ? BaseResponse.success("已删除") : BaseResponse.success("无数据");
    }

    @Override
    public Orders createOrder(HttpServletRequest request, Integer vipDay) {
        if (vipDay <= 0) {
            log.info("创建订单时的 vip天数不正确");
            return null;
        }
        LambdaQueryWrapper<VipPrice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VipPrice::getVipDays, vipDay);
        VipPrice vipPrice = vipPriceMapper.selectOne(queryWrapper);
        Orders orders;
        if (vipPrice.getVipPrice() != null) {
            Integer userId = JwtUtils.getMemberIdByJwtToken(request);
//            Integer userId=5;
            User user = redisCache.getCacheObject("login:" + userId);
            orders= new Orders();
            orders.setUserId(userId);
            orders.setOrderOn(LocalDateTime.now(ZoneOffset.of("+8")).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
            orders.setOrderTitle(user.getName() + " recharge " + vipDay + " days vip");
            orders.setVipTime(vipDay);
            orders.setOrderTotal(vipPrice.getVipPrice());//设置金额
            orders.setOrderTime(LocalDateTime.now());
            return orderMapper.insert(orders) > 0 ? orders : null;
        } else {
            log.info("充值VIP天数无对应的价格，请联系管理员添加");
            return null;
        }
    }

    @Scheduled(cron = "0 0 3 * * ?")//秒 分 时 日 月 周几
    public void updateVipState() {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPower, 0);
        List<User> users = userMapper.selectList(queryWrapper);
        for (User user : users) {
            LocalDateTime time = user.getVipOutTime();
            int num = time.compareTo(LocalDateTime.now());
            LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(User::getId, user.getId())
                    .set(num <= 0, User::getVip, 0)
                    .set(num > 0, User::getVip, 1);
            userMapper.update(null, updateWrapper);
            long timeOut = 0;
            Jedis jedis = jedisConfig.getJedis();
            if(num>0){
                Duration duration=Duration.between
                        (LocalDateTime.now(),time);
                timeOut = (duration.toMinutes())*60;
            }
            if(timeOut==0){
                jedis.del("vipOutTime:"+user.getId());
            }else {
                jedis.setex("vipOutTime:"+user.getId(), Math.toIntExact(timeOut),"");
            }
        }
        LambdaQueryWrapper<Orders> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getOrderState,"待支付");
        orderMapper.delete(wrapper);
    }

    @Override
    public BaseResponse deleteAllCollection(HttpServletRequest request) {
        Integer id = JwtUtils.getMemberIdByJwtToken(request);
        LambdaQueryWrapper<Collections> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Collections::getUserId, id);
        int delete = collectionsMapper.delete(wrapper);
        return delete > 0 ? BaseResponse.success("已删除") : BaseResponse.success("无数据");
    }

    @Override
    public BaseResponse addFollow(HttpServletRequest request, Integer id) {
        if (id == null) {
            return BaseResponse.nullValue("数据不能能为空");
        }
        LoginUser loginUser = (LoginUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = loginUser.getUser();
        if (id.equals(user.getId())) {
            return BaseResponse.success("不能关注自己");
        }
        int insert = 0;
        Integer mainId = JwtUtils.getMemberIdByJwtToken(request);
        LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follow::getUId, id).eq(Follow::getMainId, mainId);
        if (Objects.isNull(followMapper.selectOne(wrapper))) {
            insert = followMapper.insert(new Follow(null, mainId, id));
        } else {
            followMapper.delete(wrapper);
        }
        return insert > 0 ? BaseResponse.success("关注成功") : BaseResponse.success("取消关注");
    }

    @Override
    public BaseResponse getFans(Integer id, Integer nodePage, Integer pageSize) {
        if (id == null) {
            return BaseResponse.nullValue("数据不能为空");
        }
        Page<FollowDto> page = new Page<>(nodePage, pageSize);
        Page<FollowDto> page1 = followMapper.getFans(id, page);
        List<FollowDto> records = page1.getRecords();
        log.info(records+"========");
        for (FollowDto followDto : records) {
            if (followDto == null) {
                continue;
            }
            Integer id1 = followDto.getId();
            LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Follow::getMainId, id1);
            Integer integer = followMapper.selectCount(wrapper);
            followDto.setFollowNums(integer);
            LambdaQueryWrapper<Follow> wrapper1 = new LambdaQueryWrapper<>();
            wrapper1.eq(Follow::getUId, id1);
            Integer integer1 = followMapper.selectCount(wrapper1);
            followDto.setFanNums(integer1);
        }
        return page1.getSize() == 0 ? BaseResponse.success("没有粉丝") : BaseResponse.success(page1);
    }

    @Override
    public BaseResponse getUserComment(Integer id, Integer nodePage, Integer pageSize) {
        if (id == null || id <= 0) {
            return BaseResponse.error("数据错误");
        }
        if (nodePage == null || nodePage <= 0) {
            nodePage = 1;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 10;
        }
        Page<Comment> page = new Page<>(nodePage, pageSize);
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getUserId, id)
                .eq(Comment::getCommentLevel, 1);
        commentMapper.selectPage(page, queryWrapper);

        Page<CommentDto> dtoPage = new Page<>();
        BeanUtil.copyProperties(page, dtoPage, "records");
        List<Comment> pageRecords = page.getRecords();
        List<CommentDto> dtos = pageRecords.stream().map((item) -> {
            CommentDto commentDto = new CommentDto();
            BeanUtil.copyProperties(item, commentDto);
            LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Comment::getCommentId, commentDto.getId())
                    .eq(Comment::getCommentLevel, 2);
            commentDto.setCommentReplyNumber(commentMapper.selectCount(wrapper));
            return commentDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(dtos);
        return page.getTotal() != 0 ? BaseResponse.success(dtoPage) : BaseResponse.success("没有该用户的评论");
    }

    @Override
    public BaseResponse getMyVideo(HttpServletRequest request, Integer status, Integer nodePage, Integer pageSize) {
        Integer id = JwtUtils.getMemberIdByJwtToken(request);
        Page<VideoDetailDto> page = new Page<>(nodePage, pageSize);
        Page<VideoDetailDto> page1 = null;
        if (status == null || status != -1) {
           page1 = userMapper.getMyVideo(page, id, status);
        }else {
            page1 = userMapper.getMyVideo2(page, id);
        }
        return page1.getSize() == 0 ? BaseResponse.success("无数据") : BaseResponse.success(page1);
    }

    @Override
    public BaseResponse getUserById(Integer id) {
        if (id == null || id <= 0) {
            return BaseResponse.error("数据不正确");
        }
        User user = redisCache.getCacheObject("login:" + id);
        if (user == null) {
            user = userMapper.selectById(id);
            redisCache.setCacheObject("login:" + user.getId(), user, 60 * 24, TimeUnit.MINUTES);
        }
        return BaseResponse.success(user);
    }

    @Override
    public BaseResponse getFollowList(Integer id, Integer nodePage, Integer pageSize) {
        if (id == null) {
            return BaseResponse.nullValue("数据不能为空");
        }
        Page<FollowDto> page = new Page<>(nodePage, pageSize);
        Page<FollowDto> page1 = followMapper.getFollowList(id, page);
        List<FollowDto> records = page1.getRecords();
        for (FollowDto followDto : records) {
            Integer id1 = followDto.getId();
            LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Follow::getMainId, id1);
            Integer integer = followMapper.selectCount(wrapper);
            followDto.setFollowNums(integer);
            LambdaQueryWrapper<Follow> wrapper1 = new LambdaQueryWrapper<>();
            wrapper1.eq(Follow::getUId, id1);
            Integer integer1 = followMapper.selectCount(wrapper1);
            followDto.setFanNums(integer1);
        }
        return page1.getSize() == 0 ? BaseResponse.success("无数据") : BaseResponse.success(page1);
    }

    @Override
    public BaseResponse haveFollowed(Integer id, Integer mainId) {
        LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follow::getUId, id).eq(Follow::getMainId, mainId);
        return Objects.isNull(followMapper.selectOne(wrapper)) ? BaseResponse.success("未关注") : BaseResponse.success("已关注");
    }

    @Override
    public List<Integer> selectAdmin() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPower, 1);
        List<User> users = userMapper.selectList(wrapper);
        List<Integer> res = new ArrayList<>();
        for (User user : users) {
            res.add(user.getId());
        }
        return res;
    }

    @Override
    public BaseResponse deleteComment(Integer commentId) {
        if (commentId == null || commentId <= 0) {
            return BaseResponse.error("数据不正确");
        }
        try {
            LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = loginUser.getUser();
            Integer userId = user.getId();
            LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Comment::getCommentLevel, 1)
                    .eq(Comment::getId, commentId)
                    .eq(Comment::getUserId, userId);
            Comment selectOne = commentMapper.selectOne(wrapper);//查询一级评论

            if (selectOne == null) {//没有一级评论查找二级评论
                LambdaQueryWrapper<Comment> wrapper1 = new LambdaQueryWrapper<>();
                wrapper1.eq(Comment::getCommentLevel, 2)
                        .eq(Comment::getId, commentId)
                        .eq(Comment::getUserId, userId);
                Comment selectOne1 = commentMapper.selectOne(wrapper1);
                if (selectOne1 == null) {
                    return BaseResponse.error("您没有这条评论，请输入正确的数据");
                } else {//删除此二级评论的所有点赞信息后 删除该评论
                    LambdaQueryWrapper<CommentLove> clQueryWrapper = new LambdaQueryWrapper<>();
                    clQueryWrapper.eq(CommentLove::getCommentId, selectOne1.getId());
                    commentLoveMapper.delete(clQueryWrapper);
                    commentMapper.delete(wrapper1);
                    return BaseResponse.success("删除成功");
                }
            }
            LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Comment::getCommentId, commentId)
                    .eq(Comment::getCommentLevel, 2);
            List<Comment> comments = commentMapper.selectList(queryWrapper);//获取该一级评论的所有二级评论
            for (Comment comment : comments) {
                LambdaQueryWrapper<CommentLove> clQueryWrapper = new LambdaQueryWrapper<>();
                clQueryWrapper.eq(CommentLove::getCommentId, comment.getId());
                commentLoveMapper.delete(clQueryWrapper);//删除二级评论对应的点赞信息
                commentMapper.deleteById(comment.getId());//删除二级评论
            }
            LambdaQueryWrapper<CommentLove> clQueryWrapper = new LambdaQueryWrapper<>();
            clQueryWrapper.eq(CommentLove::getCommentId, commentId);
            commentLoveMapper.delete(clQueryWrapper);//删除一级评论的点赞信息
            commentMapper.delete(wrapper);//删除一级评论
            return BaseResponse.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return BaseResponse.error("删除评论出现错误");
        }
    }

    @Override
    public BaseResponse reportComment(Integer commentId, String cause) {
        if (commentId == null || commentId <= 0) {
            return BaseResponse.error("数据不正确");
        }
        if (cause.isEmpty()) {
            return BaseResponse.nullValue("请填写举报原因");
        }
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = loginUser.getUser();
        Integer userId = user.getId();
        if (userId == null) {
            return BaseResponse.errorLogin("请先登录");
        }
        CommentReport commentReport = new CommentReport();
        commentReport.setReportUserId(userId);
        commentReport.setCommentId(commentId);
        commentReport.setReportCause(cause);
        commentReport.setReportTime(LocalDateTime.now());
        Integer id = commentMapper.selectById(commentId).getUserId();
        commentReport.setCommentUserId(id);
        return commentReportMapper.insert(commentReport) > 0 ? BaseResponse.success("举报成功") : BaseResponse.error("举报出错了");
    }

    @Override
    public BaseResponse pointsExchangeVip(Integer points, Integer vipDays) {
        if (points == null || points <= 0 || vipDays == null || vipDays <= 0) {
            return BaseResponse.error("数据错误");
        }
        LambdaQueryWrapper<VipPrice> vpQueryWrapper = new LambdaQueryWrapper<>();
        vpQueryWrapper.eq(VipPrice::getVipDays, vipDays)
                .eq(VipPrice::getVipPoints, points);
        VipPrice vipPrice = vipPriceMapper.selectOne(vpQueryWrapper);
        if (vipPrice != null) {
            LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = loginUser.getUser();
            Integer userId = user.getId();
            LocalDateTime time = user.getVipOutTime();
            int num = time.compareTo(LocalDateTime.now());
            if ((user.getIntegral() - points) >= 0) {
                LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(User::getId, userId)
                        .set(User::getIntegral, (user.getIntegral() - points))
                        .set(User::getVip, 1)
                        .set(num > 0, User::getVipOutTime, time.plus(Period.ofDays(vipDays)))
                        .set(num <= 0, User::getVipOutTime, LocalDateTime.now().plus(Period.ofDays(vipDays)));
                int i = userMapper.update(null, updateWrapper);
                if (i > 0) {
                    user = userMapper.selectById(userId);
                    redisCache.setCacheObject("login:" + user.getId(), user, 60 * 24, TimeUnit.MINUTES);
                    return BaseResponse.success("兑换成功");
                } else {
                    return BaseResponse.error("兑换失败");
                }
            } else {
                return BaseResponse.success("用户积分不足");
            }
        } else {
            return BaseResponse.success("没有此兑换选项");
        }
    }

    @Override
    public BaseResponse unsubscribe() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer id = loginUser.getUser().getId();
        commentMapper.delete(new LambdaQueryWrapper<Comment>().eq(Comment::getUserId, id));
        commentLoveMapper.delete(new LambdaQueryWrapper<CommentLove>().eq(CommentLove::getUserId, id));
        feedBackMapper.delete(new LambdaQueryWrapper<FeedBack>().eq(FeedBack::getUserId, id));
        followMapper.delete(new LambdaQueryWrapper<Follow>().eq(Follow::getUId, id).or().eq(Follow::getMainId, id));
        historyMapper.delete(new LambdaQueryWrapper<History>().eq(History::getUserId, id));
        orderMapper.delete(new LambdaQueryWrapper<Orders>().eq(Orders::getUserId, id));
        remindMapper.delete(new LambdaQueryWrapper<Remind>().eq(Remind::getUId, id));
        List<Videos> videos = videoMapper.selectList(new LambdaQueryWrapper<Videos>().eq(Videos::getUserId, id));
        for (Videos video : videos) {
            videosAddressMapper.delete(new LambdaQueryWrapper<VideosAddress>().eq(VideosAddress::getVideoId, video.getId()));
            LambdaQueryWrapper<History> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(video!=null,History::getVideoId,video.getId());
            if (historyMapper.selectList(wrapper)!=null) {
                historyMapper.delete(wrapper);
            }
        }
        videoMapper.delete(new LambdaQueryWrapper<Videos>().eq(Videos::getUserId, id));
        int delete = userMapper.delete(new LambdaQueryWrapper<User>().eq(User::getId, id));
        return delete > 0 ? BaseResponse.success("已注销") : BaseResponse.success("服务器错误");
    }

    @Override
    public BaseResponse unsubscribe1(List<Integer> ids) {
        int delete = 0;
        for (Integer id : ids) {
            commentMapper.delete(new LambdaQueryWrapper<Comment>().eq(Comment::getUserId, id));
            commentLoveMapper.delete(new LambdaQueryWrapper<CommentLove>().eq(CommentLove::getUserId, id));
            feedBackMapper.delete(new LambdaQueryWrapper<FeedBack>().eq(FeedBack::getUserId, id));
            followMapper.delete(new LambdaQueryWrapper<Follow>().eq(Follow::getUId, id).or().eq(Follow::getMainId, id));
            historyMapper.delete(new LambdaQueryWrapper<History>().eq(History::getUserId, id));
            orderMapper.delete(new LambdaQueryWrapper<Orders>().eq(Orders::getUserId, id));
            remindMapper.delete(new LambdaQueryWrapper<Remind>().eq(Remind::getUId, id));
            List<Videos> videos = videoMapper.selectList(new LambdaQueryWrapper<Videos>().eq(Videos::getUserId, id));
            for (Videos video : videos) {
                videosAddressMapper.delete(new LambdaQueryWrapper<VideosAddress>().eq(VideosAddress::getVideoId, video.getId()));
                String[] split = video.getTypeName().split("，");
                for (String a : split) {
                    LambdaQueryWrapper<Type> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(Type::getTypeName,a);
                    Integer typeId = typeMapper.selectOne(wrapper).getId();
                    redisCache.deleteObject("LookedTop:"+typeId);
                }
            }
            videoMapper.delete(new LambdaQueryWrapper<Videos>().eq(Videos::getUserId, id));
            delete = userMapper.delete(new LambdaQueryWrapper<User>().eq(User::getId, id));
        }
        return delete > 0 ? BaseResponse.success("已注销") : BaseResponse.success("服务器错误");
    }

    @Override
    public BaseResponse onlineUser() {
        List<Integer> list = MyWebSocket.onlineUser();
        List<User> users = new ArrayList<>();
        if (!list.isEmpty()) {
            for (Integer id : list) {
                User user = userMapper.selectById(id);
                users.add(user);
            }
        }
        return BaseResponse.success(users);
    }

    @Override
    public BaseResponse getUserApplication(Integer status, Integer nodePage, Integer pageSize) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(status != null, User::getUploadPower, status).in(status == null, User::getUploadPower, new ArrayList<>(Arrays.asList(1, 2)));
        Page<User> page = new Page<>(nodePage, pageSize);
        Page<User> page1 = userMapper.selectPage(page, wrapper);
        return page1.getSize() == 0 ? BaseResponse.success("无数据") : BaseResponse.success(page1);
    }

    @Override
    public BaseResponse updateApplication(Integer id, Integer status) {
        if (id == null || status == null) {
            return BaseResponse.nullValue("数据不能为空");
        }
        User user = new User();
        user.setId(id);
        user.setUploadPower(status);
        int i = userMapper.updateById(user);
        return i > 0 ? BaseResponse.success("已更新") : BaseResponse.error("服务器错误");
    }

    @Override
    public BaseResponse getAllFeedBack(Integer nodePage, Integer pageSize) {
        Page<FeedBack> page = new Page<>(nodePage, pageSize);
        LambdaQueryWrapper<FeedBack> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(FeedBack::getFeedBackTime);
        Page<FeedBack> page1 = feedBackMapper.selectPage(page, wrapper);
        return page1.getSize() == 0 ? BaseResponse.success("无数据") : BaseResponse.success(page1);
    }

    @Override
    public BaseResponse deleteFeedBack(List<Integer> ids) {
        int i = feedBackMapper.deleteBatchIds(ids);
        return  i > 0 ? BaseResponse.success("已删除") : BaseResponse.success("无数据");
    }

    @Override
    public BaseResponse search(String text) {
        if(text == null || "".equals(text) || "".equals(text.trim())) {
            return BaseResponse.nullValue("数据不能为空");
        }
        List<String> res = null;
        if (redisCache.getCacheList(text).isEmpty()) {
            res = userMapper.searchVideoName(text);
            if (res == null || res.isEmpty() || res.size() == 0) {
                res = userMapper.searchTypeName(text);
                if (res == null || res.isEmpty()) {
                    res = userMapper.searchUserName(text);
                }
            }
            if (res == null || res.isEmpty()) {
                return BaseResponse.nullValue("无数据");
            }
            redisCache.setCacheList(text, res);
            redisCache.expire(text,3,TimeUnit.HOURS);
        }else {
            res = redisCache.getCacheList(text);
        }
        return res.isEmpty()? BaseResponse.nullValue("无数据") : BaseResponse.success(res);
    }

    @Override
    public BaseResponse updateWeight(Integer id, double weight) {
        if(id < 0 || id == null || weight > 100 || weight <= 0) {
            return BaseResponse.error("数据错误");
        }
        int i = advertisingMapper.updateById(new Advertising(id, null, null, null, weight));
        return i > 0 ? BaseResponse.success("已修改") : BaseResponse.error("服务器错误");
    }

    @Override
    public BaseResponse updateUserState(Integer id,Integer state,Integer days) {
        if(id == null || id <= 0)
            return BaseResponse.success("id不正确");
        if(state != 1 && state != 0)
            return BaseResponse.success("请输入正确的数据");
        User user = (User)getUserById(id).getData();
        LambdaUpdateWrapper<User> wrapper=new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId,id)
                .eq(User::getPower,0);
        int num=user.getCloseTime().compareTo(LocalDateTime.now());
        Jedis jedis = jedisConfig.getJedis();
        if(state.equals(1)){
            if(days == null || days <= 0)
                return BaseResponse.success("请输入正确的数据");
            wrapper.set(user.getUserState().equals(0),User::getUserState,1)
                    .set(num <= 0,User::getCloseTime,LocalDateTime.now().plus(Period.ofDays(days)))
                    .set(num > 0,User::getCloseTime,user.getCloseTime().plus(Period.ofDays(days)));
            //将封禁用户信息存到redis中，并设置时间过期后提醒用户，封禁结束可以正常使用账号
            long timeOut;
            if(num>0){
                Duration duration=Duration.between
                        (LocalDateTime.now(),user.getCloseTime().plus(Period.ofDays(days)));
                timeOut = (duration.toMinutes())*60;
            }else {
                timeOut = days*24*60*60;
            }
            jedis.setex("userBanned:"+id, Math.toIntExact(timeOut),"");
            String head = "封号通知";
            String body = "尊敬的"+user.getName()+"您好！" +
                    "您的账号已被封禁，您无法继续正常使用该账号，请规范使用账号！" +
                    "封禁时长为"+days+"天";
            int i = SendMailVerify.MailMessage(user.getEmail(), head, body);
            if(i==1){
                log.info("用户昵称为:"+user.getName()+"的账号解封通知已发送！");
            }else if(i==0){
                log.info("用户解封邮件发送失败！");
            }else {
                log.info("邮件信息不完整，未能发送！");
            }

        }else if(state.equals(0)){
            wrapper.set(User::getCloseTime,LocalDateTime.now())
                    .set(User::getUserState,0);
            jedis.del("userBanned:"+id);
            String head = "封号结束通知";
            String body = "尊敬的"+user.getName()+"您好！" +
                    "您的账号封禁时间已结束，您可以继续正常使用该账号，但请规范使用账号以免再次封号！";
            int i = SendMailVerify.MailMessage(user.getEmail(), head, body);
            if(i==1){
                log.info("用户昵称为:"+user.getName()+"的账号封禁通知已发送！");
            }else if(i==0){
                log.info("用户封禁邮件发送失败！");
            }else {
                log.info("邮件信息不完整，未能发送！");
            }
        }
        int i=userMapper.update(null,wrapper);
        redisCache.deleteObject("login:"+id);
        if(i>0) {
            return BaseResponse.success("设置成功");
        }
        return BaseResponse.error("设置失败");
    }

    @Override
    public BaseResponse lookUserPage(String name, String sex, String username, String email,
                                     String address,Integer nodePage,Integer pageSize) {
        if(nodePage == null || nodePage <= 0) nodePage=1;
        if(pageSize == null || pageSize<=0 || pageSize>20) pageSize=10;
        try {
            Page<User> page = new Page<>(nodePage, pageSize);
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPower, 0)
                    .like(!StringUtils.isEmpty(name), User::getName, name)
                    .like(!StringUtils.isEmpty(username), User::getUsername, username)
                    .like(!StringUtils.isEmpty(address), User::getAddress, address)
                    .like(!StringUtils.isEmpty(email),User::getEmail, email);
            if ("男".equals(sex) || "女".equals(sex))
                wrapper.eq(User::getSex, sex);
            userMapper.selectPage(page, wrapper);
            if (page.getRecords().size() != 0)
                return BaseResponse.success(page);
            return BaseResponse.success("没有符合条件的用户");
        }catch (Exception e){
            log.info("管理员查询用户出现错误");
            return BaseResponse.error("查询用户出现错误");
        }
    }
}
