package com.subhamkumar.clipsy;
import org.junit.Test;
import static org.junit.Assert.*;

import com.subhamkumar.clipsy.utils.Tools;

public class ToolsTest {


    @Test
    public void  getTimeStamp_withNoName_shouldReturnTimestamp() {

        assert(! Tools.getTimeStamp().equals(""));
    }

}
