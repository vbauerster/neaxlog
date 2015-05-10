USE [TM0011PBX]
GO

/****** Object:  Table [dbo].[CALLS]    Script Date: 02/12/2013 09:58:34 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[CALLS](
	[RecordType] [char](1) NULL,
	[TrunkRoute] [tinyint] NULL,
	[TrunkNo] [tinyint] NULL,
	[TenantNo] [tinyint] NULL,
	[Extension] [smallint] NOT NULL,
	[CalledNumber] [varchar](32) NULL,
	[AuthCode] [varchar](32) NULL,
	[CallTime] [datetime] NOT NULL,
	[Duration] [int] NULL
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[CALLS]  WITH NOCHECK ADD  CONSTRAINT [FK__CALLS__AuthCode__03317E3D] FOREIGN KEY([AuthCode])
REFERENCES [dbo].[ACODE_OWNERS] ([AuthCode])
ON UPDATE CASCADE
ON DELETE CASCADE
GO

ALTER TABLE [dbo].[CALLS] CHECK CONSTRAINT [FK__CALLS__AuthCode__03317E3D]
GO


