USE [TM0011PBX]
GO

/****** Object:  StoredProcedure [dbo].[usp_insertCall]    Script Date: 02/12/2013 09:59:45 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		Vladimir Bauer
-- Create date: 26.05.2012
-- Description:	To insert a call
-- =============================================
CREATE PROCEDURE [dbo].[usp_insertCall] 
	-- Add the parameters for the stored procedure here
	@rType char(1), 
	@trunkRoute tinyint,
	@trunkNo tinyint,
	@tenantNo tinyint,
	@ext smallint,
	@calledNum varchar(32),
	@authCode varchar(32),
	@callTime datetime,
	@duration int = 0
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	INSERT INTO CALLS
	VALUES (@rType,@trunkRoute,@trunkNo,@tenantNo,@ext,@calledNum,@authCode,@callTime,@duration)

END

GO


