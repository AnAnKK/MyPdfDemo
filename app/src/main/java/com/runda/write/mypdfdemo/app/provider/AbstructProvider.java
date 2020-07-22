package com.runda.write.mypdfdemo.app.provider;

import java.io.File;
import java.util.List;

/**
 * @Description:
 * @Author: An_K
 * @CreateDate: 2020/7/21 15:10
 * @Version: 1.0
 */
public interface AbstructProvider {
    public List<File> getList(String str);
}