package com.igormaznitsa.midp.MIDPTestMidlet.Testlet;

public interface TestletListener
{
    public void testCompleted(Testlet _testlet);
    public void testCanceled(Testlet _testlet);
    public void endTest(Testlet _testlet);
}
