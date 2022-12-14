package org.apache.tapestry.javascript;

import java.io.CharArrayWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Locale;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.TestBase;
import org.apache.tapestry.markup.MarkupWriterImpl;
import org.apache.tapestry.markup.UTFMarkupFilter;
import org.apache.tapestry.util.DescribedLocation;
import org.apache.tapestry.asset.AssetSource;
import org.apache.hivemind.Location;
import org.apache.hivemind.Resource;
import org.easymock.IAnswer;
import org.testng.annotations.Test;

import static org.easymock.EasyMock.*;

@Test
public class TestJavascriptManagerImpl extends TestBase
{
    private static final String SYSTEM_NEWLINE = (String)java.security.AccessController.doPrivileged(
            new sun.security.action.GetPropertyAction("line.separator"));
    
    public void test_null_manager()
    {
        JavascriptManagerImpl impl = createImpl();
        replay();

        assertNullAndEmpty(impl);
        
        verify();
    }

    public void test_empty_manager()
    {
        JavascriptManagerImpl impl = createImpl("", "", "", "", "", "");
        replay();

        assertNullAndEmpty(impl);

        verify();
    }

    public void test_several_files()
    {
        AssetSource source = newMock(AssetSource.class);
        expectFile(source, "a.js");
        expectFile(source, "b.js");
        expectFile(source, "tap");

        replay();

        JavascriptManagerImpl impl = createImpl(source, "a.js, b.js", "", "", "", "tap", "");
        assertEquals(impl.getAssets().size(), 2);
        assertNotNull(impl.getFirstAsset());
        assertNotNull(impl.getTapestryAsset());

        verify();
    }
    
    public void test_several_files_output()
    {
        AssetSource source = newMock(AssetSource.class);
        expectFile(source, "a.js");
        expectFile(source, "b.js");
        expectFile(source, "tap");
        IRequestCycle cycle = newRequestCycle();
        
        replay();
        
        JavascriptManager impl = createImpl(source, "a.js, b.js", "", "", "", "tap", "");
        
        CharArrayWriter charWriter = new CharArrayWriter();
        MarkupWriterImpl writer = new MarkupWriterImpl(
        		"text/html", new PrintWriter(charWriter), new UTFMarkupFilter());
        
        impl.renderLibraryResources(writer, cycle, true, true);
        impl.renderLibraryAdaptor(writer, cycle);
        
        assertEquals(charWriter.toString(), 
        		"<script type=\"text/javascript\" src=\"a.js\"></script>" + SYSTEM_NEWLINE +
        		"<script type=\"text/javascript\" src=\"b.js\"></script>" + SYSTEM_NEWLINE +
        		"<script type=\"text/javascript\" src=\"tap\"></script>" + SYSTEM_NEWLINE);

        verify();
    }    

    private void expectFile(AssetSource source, final String file) 
    {
        expect(source.findAsset((Resource) isNull(), eq(file),
                (Locale) isNull(), isA(DescribedLocation.class)))
                .andAnswer( new IAnswer<IAsset>() 
        		{
					public IAsset answer() throws Throwable {
						return new IAsset() 
						{
							public String buildURL() 
							{
								return file;
							}

							public InputStream getResourceAsStream() 
							{
								return null;
							}

							public Resource getResourceLocation() 
							{
								return null;
							}

							public Location getLocation() 
							{
								return null;
							}							
						};
					}
					
        		});               
    }

    private void assertNullAndEmpty(JavascriptManagerImpl impl) 
    {
        assertNull(impl.getPath());
        assertNull(impl.getTapestryAsset());
        assertNull(impl.getTapestryPath());
        assertNull(impl.getFirstAsset());
        assertNull(impl.getFirstFormAsset());
        assertNull(impl.getFirstWidgetAsset());
        assertTrue(impl.getAssets().isEmpty());
        assertTrue(impl.getFormAssets().isEmpty());
        assertTrue(impl.getWidgetAssets().isEmpty());
    }

    private JavascriptManagerImpl createImpl(String...params) 
    {
        return createImpl(newMock(AssetSource.class), params);
    }

    private JavascriptManagerImpl createImpl(AssetSource source, String... params) 
    {
        JavascriptManagerImpl impl = new JavascriptManagerImpl();
        impl.setAssetSource(source);

        if (params.length>0)
            impl.setFiles(params[0]);
        if (params.length>1)
            impl.setFormFiles(params[1]);
        if (params.length>2)
            impl.setWidgetFiles(params[2]);
        if (params.length>3)
            impl.setFolder(params[3]);
        if (params.length>4)
            impl.setTapestryFile(params[4]);
        if (params.length>5)
            impl.setTapestryFolder(params[5]);
        return impl;
    }
}
